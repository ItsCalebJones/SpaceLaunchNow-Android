pipeline {
    agent any

    options {
    // Stop the build early in case of compile or test failures
    skipStagesAfterUnstable()
    }

    stages{
        stage('Setup'){
            steps {
                def now = new Date()
                def date = now.format("yyMMdd.HHmm", TimeZone.getTimeZone('UTC'))
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
        stage('Build Release and Publish') {
            when {
                // Only execute this stage when building from the `beta` branch
                branch 'master'
            }
            steps {
                // Build the app in release mode, and sign the APK using the environment variables
                sh './gradlew :wear:publishBundle :mobile:publishBundle --track=internal'
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
            discordSend description: "${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\n\nMore info at: ${env.BUILD_URL}", footer: "${date}", link: env.BUILD_URL, result: currentBuild.currentResult, title: JOB_NAME, webhookURL: "https://discordapp.com/api/webhooks/641377665743323136/S0XgFaLhuNIgJFfllPxODbdWOyUD4mkSNEnFBSQZJEifdc-ClathwnpnV6uRBxJkQ71Z"
            cleanWs()
        }
    }
}