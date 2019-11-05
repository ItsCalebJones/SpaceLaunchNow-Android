pipeline {
     agent any

      options {
        // Stop the build early in case of compile or test failures
        skipStagesAfterUnstable()
      }
      
     stages{
         stage('Setup'){
             steps {
                cleanWs()
                withCredentials([file(credentialsId: 'keystore.properties', variable: 'keystoreProp')]) {
                    sh 'cp $keystoreProp keystore.properties'
                }
                withCredentials([file(credentialsId: 'Keystore', variable: 'keystoreFile')]) {
                    sh 'cp $keystoreFile spacelaunchnow.keystore'
                }
                withCredentials([file(credentialsId: 'GradleProperties', variable: 'gradleProp')]) {
                    sh 'cp $gradleProp gradle.properties'
                }
                withCredentials([file(credentialsId: 'KeysFile', variable: 'keysFile')]) {
                    sh 'cp $keysFile common/src/main/res/values/api_keys.xml'
                }
                withCredentials([file(credentialsId: 'KeysFile', variable: 'keysFile')]) {
                    sh 'cp $keysFile wear/src/main/res/values/api_keys.xml'
                }
             }
         }
        stage('Compile') {
          steps {
            // Compile the app and its dependencies
            sh './gradlew compileDebugSources'
          }
        }
         stage("Build") {
           steps {
             script {
               sh(script: "./gradlew assembleDebug",
                   returnStdout: true)
             }
           }
         }
         stage("Archive Artifacts") {
           steps {
             script {
               archiveArtifacts allowEmptyArchive: true,
                   artifacts: '**/*.apk, **/*.aab, **/build/**/mapping/**/*.txt, **/build/**/logs/**/*.txt'
               cleanWs()
             }
           }
         }
     }
 }