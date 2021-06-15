def call(Map params = [:]) {
  // Start Default Arguments
  def args = [
          NEXUS_IP               : '34.229.77.31',
  ]
  args << params

  // End Default + Required Arguments
  pipeline {
    agent {
      label "${args.SLAVE_LABEL}"
    }

    triggers {
      pollSCM('* * * * 1-5')
    }

    environment {
      COMPONENT     = "${args.COMPONENT}"
      NEXUS_IP      = "${args.NEXUS_IP}"
      PROJECT_NAME  = "${args.PROJECT_NAME}"
      SLAVE_LABEL   = "${args.SLAVE_LABEL}"
      APP_TYPE      = "${args.APP_TYPE}"
    }
    stages {

      stage('Build Code & Install Dependencies') {
        steps {
          script {
            build = new nexus()
            build.code_build("${APP_TYPE}", "${COMPONENT}")
          }
        }
      }


      stage('Prepare Artifacts') {
        steps {
          script {
            prepare = new nexus()
            prepare.make_artifacts("${APP_TYPE}", "${COMPONENT}")
          }
        }
      }

      stage('Upload Artifacts') {
        steps {
          script {
            prepare = new nexus()
            prepare.nexus(COMPONENT)
          }
        }
      }

    }

  }
}
