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

    stage('FETCHING SERVICE PROPERTIES') {
        sh "echo '****** STARTING PHASE: fetching service properties'"

        dir('secrets') {
            git branch: 'main', credentialsId: GIT_MASTER_CREDENTIALS_ID, url: 'https://github.com/bindord-org/master-secrets.git'

            /*def keyValueProps = [
                    "SVC_NAME:$SVC_NAME",
                    "PRODUCT_NAME:$PRODUCT_NAME"
            ]
            replaceVariablesInProperties(keyValueProps, BASE_CONFIGMAP)

            sh "sed -i 's/^/    /' $APPLICATION_PROPERTIES_PATH"
            sh "cat $APPLICATION_PROPERTIES_PATH >> $BASE_CONFIGMAP"*/

            sh "cat 'eureka-authentication/secret-dev.yaml'"
        }
    }


}
