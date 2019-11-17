def commitMessage() {
    def message = sh(returnStdout: true, script: "git log --format='medium' -1 ${GIT_COMMIT}").trim()
    return "${message}"
}

def projectName() {
  def jobNameParts = env.JOB_NAME.tokenize('/') as String[]
  return jobNameParts.length < 2 ? env.JOB_NAME : jobNameParts[jobNameParts.length - 2]
}

pipeline {
    agent any

    options {
    // Stop the build early in case of compile or test failures
    skipStagesAfterUnstable()
    }

    environment {
        DISCORD_URL = credentials('DiscordURL')
        COMMIT_MESSAGE = commitMessage()
        PROJECT_NAME = projectName()
    }

    stages{
        stage('Setup'){
            steps {
                withCredentials([file(credentialsId: 'keystore.properties', variable: 'keystoreProp')]) {
                    sh 'cp $keystoreProp keystore.properties'
                }
                withCredentials([file(credentialsId: 'PlaystoreKey', variable: 'playstoreKey')]) {
                    sh 'cp $playstoreKey publisher-key.json'
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
        stage('Compile Sources') {
           when {
               not {
                   branch 'master'
               }
           }
            steps {
                // Compile the app and its dependencies
                sh './gradlew compileDebugSources'
            }
        }
        stage("Assemble Debug") {
           when {
               not {
                   branch 'master'
               }
           }
           steps {
                script {
                    sh(script: "./gradlew assembleDebug",
                       returnStdout: true)
                }
           }
        }
        stage('Build Release and Publish') {
            when {
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
            discordSend description: "**Status:** ${currentBuild.currentResult}\n**Branch: **${env.BRANCH_NAME}\n**Build: **${env.BUILD_NUMBER}\n\n${COMMIT_MESSAGE}",
                        footer: "",
                        link: env.BUILD_URL,
                        result: currentBuild.currentResult,
                        title: PROJECT_NAME,
                        webhookURL: DISCORD_URL,
                        thumbnail: "https://i.imgur.com/UZTtsSR.png",
                        notes: "Hey <@&641718676046872588>, new build completed for ${PROJECT_NAME}!"
            cleanWs()
        }
    }
}