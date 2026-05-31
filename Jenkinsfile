pipeline {
   // agent { label 'docker-agent' }
    agent any

    tools {
        maven 'maven3'
    }

    options {
        buildDiscarder logRotator(daysToKeepStr: '15', numToKeepStr: '10')
    }


    environment {
        APP_NAME           = "INTEGRACION_APP"
        APP_ENV            = "MAIN"
        SONARQUBE_ENV      = "SonarQube25"
        SLACK_WEBHOOK_URL  = credentials('slackWebhook')
    }

    stages {
        stage('Checkout Código') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/feature/integraciontest']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/henrymerino/integracion.git',
                        credentialsId: 'gitIntegracion'
                    ]]
                ])
            }
        }

        stage('Compilación') {
            steps {
                sh 'mvn clean install -Dmaven.test.skip=true'
            }
        }

        stage('Análisis con SonarQube') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=integracion -Dsonar.host.url=http://host.docker.internal:9000'
                }
            }
        }

        stage('Esperar resultados de SonarQube') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    script {
                        def qualityGate = waitForQualityGate()
                        if (qualityGate.status != 'OK') {
                            error "❌ Quality Gate no aprobado: ${qualityGate.status}"
                        }
                        currentBuild.description = 'QualityGate: OK'
                    }
                }
            }
        }

        stage('Archivar artefactos') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar, workspace_test.txt', fingerprint: true
            }
        }

        stage('Mostrar variables de entorno') {
            steps {
                sh 'env'
            }
        }

        stage('Validar y hacer merge a main') {
            when {
                expression {
                    return currentBuild.description == 'QualityGate: OK'
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'gitIntegracion', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {
                    sh '''
                        git config user.name "$GIT_USER"
                        git config user.email "haguilarmerino@gmail.com"

                        git remote set-url origin https://$GIT_USER:$GIT_TOKEN@github.com/henrymerino/integracion.git

                        git fetch origin
                        git checkout main
                        git pull origin main

                        git merge origin/feature/integraciontest --no-ff -m "Merge automático desde Jenkins" || exit 1

                        git push origin main
                    '''
                }
            }
        }

        stage('Verificar Docker') {
            steps {
                sh '''
                    hostname
                    whoami
                    which docker
                    docker --version
                    docker ps
                '''
            }
        }

        stage('Diagnóstico Agente') {
            steps {
                sh '''
                    echo "HOSTNAME:"
                    hostname

                    echo "USER:"
                    whoami

                    echo "DOCKER:"
                    which docker || true

                    echo "PATH:"
                    echo $PATH
                '''
            }
        }

        stage('Diagnóstico Docker') {
            steps {
                sh '''
                    echo "===== USUARIO ====="
                    whoami

                    echo "===== ID ====="
                    id

                    echo "===== DIRECTORIO ====="
                    pwd

                    echo "===== PATH ====="
                    echo $PATH

                    echo "===== DOCKER ====="
                    which docker || true

                    echo "===== VERSION DOCKER ====="
                    docker --version || true

                    echo "===== SOCKET ====="
                    ls -l /var/run/docker.sock || true
                '''
            }
        }

		stage('Build Docker Image') {
			when {
				expression {
					return currentBuild.description == 'QualityGate: OK'
				}
			}
			//${BUILD_NUMBER} es una variable de entorno automática de Jenkins que contiene el número consecutivo de la ejecución actual del pipeline.
			steps {
				sh '''
					docker build -t integracion-app:${BUILD_NUMBER} .
					docker tag integracion-app:${BUILD_NUMBER} integracion-app:latest
				'''
			}
		}

		stage('Deploy Docker Container') {
			when {
				expression {
					return currentBuild.description == 'QualityGate: OK'
				}
			}
			steps {
				sh '''
					docker stop integracion-app || true
					docker rm integracion-app || true

					docker run -d \
						--name integracion-app \
						-p 8080:8080 \
						integracion-app:latest
				'''
			}
	    }


}


post {
        success {
            slackNotify("✅ *Pipeline exitoso* `${env.JOB_NAME}` #${env.BUILD_NUMBER} - <${env.BUILD_URL}|Ver detalles>")
        }
        failure {
            slackNotify("❌ *Pipeline fallido* `${env.JOB_NAME}` #${env.BUILD_NUMBER} - <${env.BUILD_URL}|Ver detalles>")
        }
    }
}

def slackNotify(String message) {
    sh """
        curl -X POST -H 'Content-type: application/json' \
        --data '{\"text\": \"${message}\"}' \
        ${env.SLACK_WEBHOOK_URL}
    """
}
