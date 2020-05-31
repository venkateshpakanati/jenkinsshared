
public class PodTemplate implements Serializable {
    public String clustername
    public String namespace
    public String podlabel
    public String workingdir
    public String memLmt
    public String cpuLmt
    public String buildWrkspace
    def script
    public Map images
    def inputcontainers = []

    public PodTemplate(String clustername,
                      String namespace,
                      String label,
                      Map images,
                      String workingdir,
                      script) {
        this.clustername=clustername
        this.namespace=namespace 
        this.podlabel=label
        this.workingdir=workingdir
        this.script=script
        this.images = images

      if (images.containsKey('maven')) {
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
        if (images.containsKey('helm')) {
          cpuLmt = '500m'
          memLmt = '500Mi'
          if (images.containsKey('helmCpuLmt')) {
            cpuLmt = images."helmCpuLmt"
          }
          if (images.containsKey('helmMemLmt')) {
            memLmt = images."helmMemLmt"
          }
          this.inputcontainers << 
            script.containerTemplate(
              name: 'helm', 
              image: images."helm",
              command: 'cat', 
              ttyEnabled: true,
             // workingDir: workingdir,
              alwaysPullImage: false,
              resourceRequestCpu: '100m',
              resourceLimitCpu: cpuLmt,
              resourceRequestMemory: '500Mi',
              resourceLimitMemory: memLmt
            )
        }
         if (images.containsKey('docker')) {
          cpuLmt = '500m'
          memLmt = '500Mi'
          if (images.containsKey('dockerCpuLmt')) {
            cpuLmt = images."dockerCpuLmt"
          }
          if (images.containsKey('dockerMemLmt')) {
            memLmt = images."dockerMemLmt"
          }
          this.inputcontainers << 
            script.containerTemplate(
              name: 'docker', 
              image: images."docker",
              command: 'cat', 
              ttyEnabled: true,
             // workingDir: workingdir,
              alwaysPullImage: false,
              resourceRequestCpu: '100m',
              resourceLimitCpu: cpuLmt,
              resourceRequestMemory: '500Mi',
              resourceLimitMemory: memLmt
            )
        }
        if (images.containsKey('jnlp')) {
            cpuLmt = '800m'
            memLmt = '1500Mi'
          if (images.containsKey('jnlpCpuLmt')) {
            cpuLmt = images."jnlpCpuLmt"
          }
          if (images.containsKey('jnlpMemLmt')) {
              memLmt = images."jnlpMemLmt"
          }
          this.inputcontainers  << 
              script.containerTemplate(
                name: 'jnlp', 
                image: images."jnlp",                        
                args: '${computer.jnlpmac} ${computer.name}',
               // workingDir: workingdir,
                resourceRequestCpu: '50m',
                resourceLimitCpu: cpuLmt,
                resourceRequestMemory: '512Mi',
                resourceLimitMemory: memLmt
              )
        }  
     }
     public void BuilderTemplate (body) {
      script.podTemplate(
          cloud: clustername,
          namespace: namespace,
          label: podlabel,
          containers: this.inputcontainers,
          volumes: [
           // script.secretVolume(secretName: 'maven-settings', mountPath: "${workingdir}/.m2")
           script.configMapVolume(configMapName: "settings-xml", mountPath: '/home/jenkins/.m2'),
           script.hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
           script.secretVolume(secretName: 'helm-repository', mountPath: '/home/groot/helm/repository'),
           script.emptyDirVolume(mountPath: '/home/groot/helm/repository/cache', memory: false)
          ]
       ){
            body ()
       }
  }

}
