pipeline {
    agent any

    options {
    // Stop the build early in case of compile or test failures
    skipStagesAfterUnstable()
    }

    stages{
        stage('Setup'){
            steps {
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
        stage("Assemble Debug") {
            steps {
                script {
                    sh(script: "./gradlew assembleDebug",
                       returnStdout: true)
                }
            }
        }
        stage('Build Release') {
            when {
                // Only execute this stage when building from the `beta` branch
                branch 'master'
            }
            steps {
                // Build the app in release mode, and sign the APK using the environment variables
                sh './gradlew assembleRelease'

                // Upload the APK to Google Play
                // androidApkUpload googleCredentialsId: 'Google Play', apkFilesPattern: '**/*-release.apk', trackName: 'beta'
            }
            post {
                success {
                  // Notify if the upload succeeded
                  mail to: 'beta-testers@example.com', subject: 'New build available!', body: 'Check it out!'
                }
            }
        }
        stage("Archive Artifacts") {
            steps {
                script {
                    archiveArtifacts allowEmptyArchive: true,
                       artifacts: '**/*.apk, **/*.aab, **/build/**/mapping/**/*.txt, **/build/**/logs/**/*.txt'

                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}