pipeline {
     agent any

     stages{
         stage('Setup'){
             steps {
                 withCredentials([file(credentialsId: 'keystore.properties', variable: 'keystoreProp')]) {
                     sh 'cp $keystoreProp keystore.properties'
                 }
                 withCredentials([file(credentialsId: 'Keystore', variable: 'keystoreFile')]) {
                     sh 'cp $keystoreFile spacelaunchnow.keystore'
                 }
                  withCredentials([file(credentialsId: 'KeysFile', variable: 'keysFile')]) {
                      sh 'cp $keysFile common/src/main/res/values/api_keys.xml'
                  }
             }
         }
         stage("Build") {
           steps {
             script {
               sh(script: "./gradlew clean :mobile:bundleRelease :mobile:assembleRelease",
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