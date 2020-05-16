
def buildAndPublishChart(String chartPath, String releasename, String helmvirtualrepo) {
                      chartPath = "projectchart"
                      releasename ="projectchart";
                      helmvirtualrepo = "local";
                      sh '''
                        cat /home/groot/helm/repository/repositories.yaml
                        helm repo add helm http://172.42.42.104:8081/artifactory/helm --username admin --password AP9YMHJpDaRrnUzzyY7e452G742
                        helm repo update --debug
                        helm repo list --debug
                      '''
                      sh "yq w -i projectchart/Chart.yaml version ${env.BUILD_ID}"
                      sh "yq w -i projectchart/Chart.yaml appVersion ${env.BUILD_ID}"
                      sh "yq w -i projectchart/values.yaml image.tag ${env.BUILD_ID}"
                                  
                      sh '''                                
                        chart_name="projectchart"
                        version=$(helm inspect "$chart_name" | yq r - 'version')
                        helm package projectchart
                        ls -lrt
                        chart_filename="${chart_name}-${version}.tgz"
                        curl -u admin:AP9YMHJpDaRrnUzzyY7e452G742 -X PUT -vvv -T "${chart_filename}" "http://172.42.42.104:8081/artifactory/helm/${chart_filename}"
                      '''
}
     

def deploy(){
                    sh '''
                      helm repo add helm http://172.42.42.104:8081/artifactory/helm --username admin --password AP9YMHJpDaRrnUzzyY7e452G742
                      helm repo update --debug
                    '''	  
                    sh "helm upgrade projectchart http://172.42.42.104:8081/artifactory/helm/projectchart-${env.BUILD_ID}.tgz  --username admin --password AP9YMHJpDaRrnUzzyY7e452G742 --install --force --debug"
                    
}