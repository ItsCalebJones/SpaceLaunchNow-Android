pipeline {
    agent any
    stage('Setup'){
        steps {
            withCredentials([file(credentialsId: 'keystore.properties', variable: 'keystore.properties')]) {
                sh 'cp $configFile ~/keystore.properties'
            }
            withCredentials([file(credentialsId: 'Keystore', variable: 'spacelaunchnow.keystore')]) {
                sh 'cp $spacelaunchnow.keystore ~/spacelaunchnow.keystore'
            }
        }
    }
    stage("Build") {
      steps {
        script {
          sh(script: "./gradlew clean :mobile:bundleRelease :app:assembleRelease",
              returnStdout: true)
        }
      }
    }
    stage("Archive Artifacts") {
      steps {
        script {
          archiveArtifacts allowEmptyArchive: true,
              artifacts: '**/*.apk, **/*.aab, **/build/**/mapping/**/*.txt,
               **/build/**/logs/**/*.txt'
          cleanWs()
        }
      }
    }
}