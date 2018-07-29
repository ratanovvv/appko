podTemplate(label: 'pod', cloud: 'cluster-1', containers: [
    containerTemplate(name: "git-${env.BUILD_NUMBER}", image: 'alpine/git', command: 'cat', ttyEnabled: true),
    containerTemplate(name: "terraform-${env.BUILD_NUMBER}", image: 'hashicorp/terraform', ttyEnabled: true, command: 'cat',
        envVars: [
            envVar(key: "GOOGLE_PROJECT", value: "ratanovvv"),
            envVar(key: "GOOGLE_REGION", value: "us-central1-f")
    ])
  ]) {
  node('pod') {
	stage('checkout'){
        container('git'){
            dir('scm'){deleteDir()}
            dir('scm'){
                git url: 'https://github.com/ratanovvv/appko.git'
                stash 'scm-files'
            }
        }
	}
    stage('terraform'){
        container("terraform-${env.BUILD_NUMBER}"){
            dir('terraform'){deleteDir()}
            dir('terraform'){
                unstash 'scm-files'
        		withCredentials([file(credentialsId: 'ratanovvv-secretfile', variable: 'GOOGLE_APPLICATION_CREDENTIALS'),
        		                 file(credentialsId: 'rvv-ssh-key-pub', variable: 'TF_VAR_ssh_key')]) {
        		    sh """
        		    echo "\$TF_VAR_ssh_key \$GOOGLE_APPLICATION_CREDENTIALS \$GOOGLE_PROJECT \$GOOGLE_REGION"
        		    cd terraform/init && terraform init && terraform apply -auto-approve || true && \
        		    cd ../vm && until terraform init; do echo "sleep..."; sleep 20; done && terraform apply -auto-approve
        			"""
                }
            }
        }
    }
    stage('check vm'){
		withCredentials([file(credentialsId: 'rvv-ssh-key', variable: 'SSH_KEY')]) {
		    unstash 'scm-files'
            sh """
            _ip_address=\$(grep address terraform/vm/vm.tf | awk '{print \$3}' | sed 's/\"//g')
            sleep 20 && ssh -o StrictHostKeyChecking=no -i \$SSH_KEY rvv@\$_ip_address uname -a
            """
          }
		}
	}
}
