// Jenkinsfile
// Runs entirely as ephemeral pods inside the Kubernetes cluster where Jenkins
// itself lives. Requires the "Kubernetes" Jenkins plugin (bundled with the
// official Jenkins Helm chart) and the RBAC in k8s/jenkins-rbac.yaml applied.

def REGISTRY   = "registry.registry.svc.cluster.local:5000"
def REGISTRY_EXTERN = "localhost:30500"
def WEB_SERVER_IMAGE_NAME = "web-server-prod"
def WORKER_IMAGE_NAME = "worker-prod"
def WEB_SERVER_DEPLOYMENT_NAME= "web-server"
def WORKER_DEPLOYMENT_NAME= "worker"
def APP_NS     = "fwc"

pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: jenkins-deployer
  containers:
    - name: kaniko
      image: gcr.io/kaniko-project/executor:debug
      command: ["/busybox/cat"]
      tty: true
    - name: kubectl
      image: bitnami/kubectl:latest
      command: ["cat"]
      tty: true
      securityContext:
        runAsUser: 1000
    - name: node
      image: node:18
      command: ["cat"]
      tty: true
      securityContext:
        runAsUser: 1000
    - name: sbt
      image: sbtscala/scala-sbt:eclipse-temurin-jammy-17.0.10_7_1.9.9_2.12.19
      command: ["cat"]
      tty: true
      securityContext:
        runAsUser: 1000

"""
        }
    }

    triggers {
        // Requires the "Generic Webhook Trigger" or GitHub/GitLab plugin
        // configured to POST to Jenkins on push. See README for webhook setup.
        githubPush()
    }

    environment {
        IMAGE_TAG = "${env.GIT_COMMIT.take(7)}"
    }


    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // --- Optional: language-specific build/test step -------------------
        // Since the project mixes languages/build tools, the actual
        // compile/test step is expected to happen either:
        //   (a) inside the Dockerfile itself (recommended, multi-stage build), or
        //   (b) here, in an extra container in the pod spec above
        //       (e.g. add a "maven" or "node" container and `sh` into it).
        // Leaving this stage as a placeholder / hook.
        stage('Build (pre-Docker)') {
            steps {
                // echo 'Installing dependencies and building the webserver.'
                // container('node') {
                //     sh """
                //         cd web-server &&\
                //         npm i &&\
                //         npx nest build
                //     """
                // }
                echo 'Installing dependencies and building the worker.'
                container('sbt') {
                    sh """
                        cd game-logic-core &&\
                        sbt update &&\
                        sbt assembly
                    """
                }
            }
        }

        stage('Build & Push Image (Kaniko)') {
            steps {
                container('kaniko') {
                    // echo "Building web-server image"
                    // sh """
                    //     /kaniko/executor \
                    //     --context=`pwd`/web-server \
                    //     --dockerfile=`pwd`/web-server/Dockerfile \
                    //     --destination=${REGISTRY}/${WEB_SERVER_IMAGE_NAME}:${IMAGE_TAG} \
                    //     --destination=${REGISTRY}/${WEB_SERVER_IMAGE_NAME}:latest \
                    //     --insecure \
                    //     --insecure-pull \
                    //     --skip-tls-verify
                    // """
                    echo "Building worker image"
                    sh """
                        /kaniko/executor \
                        --context=`pwd`/game-logic-core/docker \
                        --dockerfile=`pwd`/game-logic-core/docker/Dockerfile \
                        --destination=${REGISTRY}/${WORKER_IMAGE_NAME}:${IMAGE_TAG} \
                        --destination=${REGISTRY}/${WORKER_IMAGE_NAME}:latest \
                        --insecure \
                        --insecure-pull \
                        --skip-tls-verify
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                container('kubectl') {
                    echo "Image tag ${IMAGE_TAG}\n"
                    // echo "Deploying web-server image"
                    // sh """
                    //     kubectl set image deployment/${WEB_SERVER_DEPLOYMENT_NAME} \
                    //     ${WEB_SERVER_DEPLOYMENT_NAME}=${REGISTRY_EXTERN}/${WEB_SERVER_IMAGE_NAME}:${IMAGE_TAG} \
                    //     -n ${APP_NS}

                    //     kubectl rollout status deployment/${WEB_SERVER_DEPLOYMENT_NAME} -n ${APP_NS} --timeout=120s
                    // """
                    echo "Deploying worker image"
                    sh """
                        kubectl set image deployment/${WORKER_DEPLOYMENT_NAME} \
                        ${WORKER_DEPLOYMENT_NAME}=${REGISTRY_EXTERN}/${WORKER_IMAGE_NAME}:${IMAGE_TAG} \
                        -n ${APP_NS}

                        kubectl rollout status deployment/${WORKER_DEPLOYMENT_NAME} -n ${APP_NS} --timeout=120s
                    """
                }
            }
        }
    }
    

    post {
        failure {
            echo "Pipeline failed — deployment was not updated."
        }
        success {
            echo "Deployed ${WEB_SERVER_IMAGE_NAME}:${IMAGE_TAG} to namespace ${APP_NS}"
            echo "Deployed ${WORKER_IMAGE_NAME}:${IMAGE_TAG} to namespace ${APP_NS}"
        }
    }
}
