public class PodTemplate implements Serializable {
    public String podlabel
    public String workingdir
    public String memLmt
    public String cpuLmt
    public String m2FileSystem
    public String buildWrkspace
    def script
    public Map images
    def inputcontainers = []

    public PodTemplate(String label,
                      Map images,
                      String workingdir,
                      script) {
        this.podlabel=label
        this.workingdir=workingdir
        this.script=script
        this.images = images

      if (images.containsKey('maven')) {
          println "it's maven image >>>>>>>>>>>>>>>>>>>>>>"
          cpuLmt = '500m'
          memLmt = '500Mi'
          if (images.containsKey('mavenCpuLmt')) {
            cpuLmt = images."mavenCpuLmt"
          }
          if (images.containsKey('mavenMemLmt')) {
            memLmt = images."mavenMemLmt"
          }
          this.inputcontainers  <<
            script.containerTemplate(
              name: 'maven',
              image: images."maven",
              command: 'cat',
              envVars: [
                //script.envVar(key: 'JAVA_TOOL_OPTIONS', value: "-Duser.home=${workingdir}"),
                script.envVar(key: 'MAVEN_CONFIG', value: '${workingdir}/.m2')
              ],
              ttyEnabled: true,
             // workingDir: workingdir,
              alwaysPullImage: false,
              resourceRequestCpu: '100m',
              resourceLimitCpu: cpuLmt,
              resourceRequestMemory: '500Mi',
              resourceLimitMemory: memLmt
            )
        }
     }
// podTemplate(label: label, containers: [ 
//   containerTemplate(name: 'maven', image: 'maven:3.6.0-jdk-8-alpine', command: 'cat', ttyEnabled: true,
//   envVars: [envVar(key: 'MAVEN_CONFIG', value: '/home/jenkins/.m2')]),
//   containerTemplate(name: 'docker', image: 'trion/jenkins-docker-client'),
// //  containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.8.8', command: 'cat', ttyEnabled: true),
//   containerTemplate(name: 'helm', image: 'venkateshpakanati/helm_push:1.3', command: 'cat', ttyEnabled: true)

//   ],
//   volumes: [
//       configMapVolume(configMapName: 'settings-xml', mountPath: '/home/jenkins/.m2'),
//       hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
//       secretVolume(secretName: 'helm-repository', mountPath: '/home/groot/helm/repository'),
//       emptyDirVolume(mountPath: '/home/groot/helm/repository/cache', memory: false)
//   ]
     public void BuilderTemplate (body) {
      script.podTemplate(
          label: podlabel,
          containers: this.inputcontainers,
          volumes: [
           // script.secretVolume(secretName: 'maven-settings', mountPath: "${workingdir}/.m2")
           script.configMapVolume(configMapName: "settings-xml", mountPath: '/home/jenkins/.m2'),
          ]
       ){
            body ()
       }
  }

}
