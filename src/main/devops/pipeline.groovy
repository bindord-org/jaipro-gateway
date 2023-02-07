String getPropValueFromProperties(String property) {
    return sh(script: "cat src/main/resources/application.properties " +
            "| grep '$property=' " +
            "| awk -F '$property=' '{print \$2}'",
            returnStdout: true).trim()
}

void replaceVariablesInProperties(property, propertyFile, Boolean pipe = false) {
    sh "echo '*** Init replaceVariablesInProperties'"

    def commandString = "sed"
    property.each { item ->
        def keyVal = item.split(':', 2)
        if (!pipe) {
            commandString += " -e \"s/\\${keyVal[0]}/${keyVal[1]}/\""
        } else {
            commandString += " -e \"s|\\${keyVal[0]}|${keyVal[1]}|\""
        }
    }
    commandString += " -i $propertyFile"

    sh "echo 'commandString: $commandString'"
    sh "$commandString"
    sh "cat $propertyFile"
}

node {

    // FUNDAMENTAL_PROPS
    def GIT_MASTER_CREDENTIALS_ID = 'GITHUB_PPCC'
    def MASTER_FOLDER = 'master'
    def SECRET_FOLDER = 'secret'
    def DEPLOY_ENV = 'dev'
    def K8S_LOCAL = 'K8S_CONFIG_ID_LOCAL'
    // - BASE PATHS
    def JOB_NAME = env.JOB_NAME
    def JOB_FULLPATH = env.WORKSPACE
    def BASE_CONFIGMAP = 'base/config-map-base.yaml'
    def MVN_REPOSITORY = '/root/.m2'

    // SERVICE PROPS
    def SVC_REPOSITORY_URL = scm.userRemoteConfigs[0].url
    def PRODUCT_NAME = 'hogarep'
    def SVC_FOLDER = 'app'
    def SVC_NAME = ''
    def APPLICATION_PROPERTIES_PATH = ''
    def SVC_FULLPATH = '/home/ubuntu/jenkins/jenkins_home/workspace' + '/' + JOB_NAME + '/' + SVC_FOLDER

    // K8S FILES
    def COMMON_PATH = 'src/main/devops'
    def SVC_DEPLOYMENT = "$COMMON_PATH/deployment.yaml"
    def SVC_SERVICE = "$COMMON_PATH/service.yaml"
    def SVC_INGRESS = "$COMMON_PATH/ingress.yaml"
    def SVC_HPA = "$COMMON_PATH/hpa.yaml"

    //DOCKER REGISTRY PROPS
    def CR_BINDORD_HOST = "peterzinho16"
    def SVC_IMAGE = ''
    def SVC_DOCKERFILE = "$COMMON_PATH/Dockerfile"

    stage('PRINT VARIABLES') {
        sh "echo 'SVC_REPOSITORY_URL: $SVC_REPOSITORY_URL'"
        sh "echo 'JOB_FULLPATH: $JOB_FULLPATH'"
        sh "echo 'JOB_NAME: $JOB_NAME'"
        sh "echo '******INITIALIZING.....'"
    }

    stage('FETCHING SERVICE SOURCES') {
        sh "echo '****** STARTING PHASE: fetching service sources'"

        dir(SVC_FOLDER) {
            git branch: 'main', credentialsId: GIT_MASTER_CREDENTIALS_ID, url: SVC_REPOSITORY_URL

            SVC_NAME = getPropValueFromProperties('spring.application.name')
            APPLICATION_PROPERTIES_PATH = "$SVC_NAME/application-$DEPLOY_ENV" + ".yaml"
        }
    }

    stage('FETCHING SERVICE PROPERTIES') {
        sh "echo '****** STARTING PHASE: fetching service properties'"

        dir(MASTER_FOLDER) {
            git branch: 'main', credentialsId: GIT_MASTER_CREDENTIALS_ID, url: 'https://github.com/bindord-org/master-properties.git'

            def keyValueProps = [
                    "SVC_NAME:$SVC_NAME",
                    "PRODUCT_NAME:$PRODUCT_NAME"
            ]
            replaceVariablesInProperties(keyValueProps, BASE_CONFIGMAP)

            sh "sed -i 's/^/    /' $APPLICATION_PROPERTIES_PATH"
            sh "cat $APPLICATION_PROPERTIES_PATH >> $BASE_CONFIGMAP"
            sh "cat $BASE_CONFIGMAP"
        }

        dir(SECRET_FOLDER) {
            git branch: 'main', credentialsId: GIT_MASTER_CREDENTIALS_ID, url: 'https://github.com/bindord-org/master-secrets.git'

            def keyValueProps = [
                    "SVC_NAME:$SVC_NAME",
                    "PRODUCT_NAME:$PRODUCT_NAME"
            ]
            replaceVariablesInProperties(keyValueProps, BASE_CONFIGMAP)

            sh "sed -i 's/^/    /' $APPLICATION_PROPERTIES_PATH"
            sh "cat $APPLICATION_PROPERTIES_PATH >> $BASE_CONFIGMAP"
            sh "cat $BASE_CONFIGMAP"
        }
    }

    stage('DEPLOYING CONFIGMAP & SECRETS') {
        sh 'echo "INIT K8S...."'

        def BASE_SECRETMAP = "$SVC_NAME/secret-$DEPLOY_ENV"+".yaml"

        withKubeConfig([credentialsId: K8S_LOCAL]) {
            sh "kubectl apply -f $MASTER_FOLDER/$BASE_CONFIGMAP"

            sh "kubectl apply -f $SECRET_FOLDER/$BASE_SECRETMAP"
        }
    }

    /*stage('TESTING') {
        sh "echo '****** STARTING PHASE: testing'"

        sh "docker run -i --rm -p 8080:8080 " +
                "-v $SVC_FULLPATH:/$SVC_FOLDER " +
                "-v $MVN_REPOSITORY:$MVN_REPOSITORY " +
                "-w /$SVC_FOLDER " +
                "maven:3.8.1-openjdk-11-slim " +
                "mvn test"
    }*/

    stage('COMPILING AND PUSHING IMAGE') {
        sh "echo '****** STARTING PHASE: compiling and pushing image'"

        sh "docker run -i --rm -p 8080:8080 " +
                "-v $SVC_FULLPATH:/$SVC_FOLDER " +
                "-v $MVN_REPOSITORY:$MVN_REPOSITORY " +
                "-w /$SVC_FOLDER " +
                "maven:3.8.1-openjdk-11-slim " +
                "mvn clean package"

        def SVC_VERSION = sh(script: "cat $SVC_FOLDER/pom.xml " +
                '| grep -B 1 \'name\' ' +
                '| grep \'<version>\' ' +
                '| sed -e \'s/^[[:space:]]*//\' | cut -c 10- | rev | cut -c 11- | rev',
                returnStdout: true).trim()

        SVC_IMAGE = "$CR_BINDORD_HOST/$SVC_NAME:$SVC_VERSION"

        def SVC_JAR_NAME_PARAM = '${SVC_JAR_NAME}'

        def keyValueProps = [
                "$SVC_JAR_NAME_PARAM:$SVC_NAME-$SVC_VERSION",
        ]

        replaceVariablesInProperties(keyValueProps, "./$SVC_FOLDER/$SVC_DOCKERFILE")

        sh "echo 'SVC_VERSION: ${SVC_VERSION}--'"
        sh "docker build " +
                "-t $SVC_IMAGE " +
                "-f ./$SVC_FOLDER/src/main/devops/Dockerfile " +
                "./$SVC_FOLDER/target"
    }

    stage('DEPLOYING TO K8S') {

        dir(SVC_FOLDER) {

            def IMAGE_PARAM = '${SVC_IMAGE}'
            def SVC_NAME_PARAM = '${SVC_NAME}'

            def keyValueProps = [
                    "$IMAGE_PARAM:$SVC_IMAGE",
                    "$SVC_NAME_PARAM:$SVC_NAME"
            ]

            replaceVariablesInProperties(keyValueProps, SVC_DEPLOYMENT, true)

            withKubeConfig([credentialsId: K8S_LOCAL]) {
                sh "kubectl apply -f $SVC_DEPLOYMENT"
            }
        }

    }

    stage('EXPOSING SERVICE') {

        dir(SVC_FOLDER) {

            def SVC_NAME_PARAM = '${SVC_NAME}'

            def keyValueProps = [
                    "$SVC_NAME_PARAM:$SVC_NAME"
            ]

            replaceVariablesInProperties(keyValueProps, SVC_SERVICE)

            def CONTEX_PATH_PARAM = 'SERVICE_INGRESS_CONTEXT_PATH'

            def SVC_CONTEXT_PATH = getPropValueFromProperties('service.ingress.context-path')

            def keyValuePropsTwo = [
                    "$CONTEX_PATH_PARAM:$SVC_CONTEXT_PATH",
                    "$SVC_NAME_PARAM:$SVC_NAME"
            ]

            replaceVariablesInProperties(keyValuePropsTwo, SVC_INGRESS, true)

            def keyValuePropsThree = [
                    "$SVC_NAME_PARAM:$SVC_NAME"
            ]

            replaceVariablesInProperties(keyValuePropsThree, SVC_HPA)

            withKubeConfig([credentialsId: K8S_LOCAL]) {
                sh "kubectl apply -f $SVC_SERVICE"
                sh "kubectl apply -f $SVC_INGRESS"
                sh "kubectl apply -f $SVC_HPA"
            }

        }

    }


}
