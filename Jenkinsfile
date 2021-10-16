def commitMessage() {
    def message = sh(returnStdout: true, script: "git log --format='medium' -1 ${GIT_COMMIT}").trim()
    return "${message}"
}

def projectName() {
  def jobNameParts = env.JOB_NAME.tokenize('/') as String[]
  return jobNameParts.length < 2 ? env.JOB_NAME : jobNameParts[jobNameParts.length - 2]
}

class Constants {

    static final String MASTER_BRANCH = 'master'

    static final String QA_BUILD = 'Debug'
    static final String RELEASE_BUILD = 'Release'

    static final String INTERNAL_TRACK = 'internal'
    static final String RELEASE_TRACK = 'alpha'
}

def getBuildType() {
    switch (env.BRANCH_NAME) {
        case Constants.MASTER_BRANCH:
            return Constants.RELEASE_BUILD
        default:
            return Constants.QA_BUILD
    }
}

def getTrackType() {
    switch (env.BRANCH_NAME) {
        case Constants.MASTER_BRANCH:
            return Constants.RELEASE_TRACK
        default:
            return Constants.INTERNAL_TRACK
    }
}

pipeline {
    agent any

    options {
    // Stop the build early in case of compile or test failures
    skipStagesAfterUnstable()
        buildDiscarder(
            logRotator(
                // number of build logs to keep
                numToKeepStr:'10',
                // history to keep in days
                daysToKeepStr: '15',
                // artifacts are kept for days
                artifactDaysToKeepStr: '15',
                // number of builds have their artifacts kept
                artifactNumToKeepStr: '5'
            )
        )
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
                withCredentials([file(credentialsId: 'GoogleServiceJson', variable: 'googleServiceJson')]) {
                    sh 'cp $googleServiceJson mobile/google-services.json'
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
                sh './gradlew publishBundle --track=internal'
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
            sh '''
               rm common/src/main/res/values/api_keys.xml
               rm gradle.properties
               rm spacelaunchnow.keystore
               rm keystore.properties
               rm publisher-key.json
               '''
        }
    }
}