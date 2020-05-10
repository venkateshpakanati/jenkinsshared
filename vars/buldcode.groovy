public class test implements Serializable {
    public String podlabel
    public String workingdir
    public String memLmt
    public String cpuLmt
    public String m2FileSystem
    public String buildWrkspace
    def script
    public Map images
    def inputcontainers = []

    public test(String label,
                      Map images,
                      String workingdir,
                      script) {
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
                script.envVar(key: 'JAVA_TOOL_OPTIONS', value: "-Duser.home=${workingdir}"),
                script.envVar(key: 'MAVEN_CONFIG', value: "${workingdir}/.m2")
              ],
              ttyEnabled: true,
              workingDir: workingdir,
              alwaysPullImage: false,
              resourceRequestCpu: '100m',
              resourceLimitCpu: cpuLmt,
              resourceRequestMemory: '500Mi',
              resourceLimitMemory: memLmt
            )
        }
     }

     public void BuilderTemplate (body) {
      script.podTemplate(
          label: podlabel,
          containers: this.inputcontainers,
          volumes: [
            //script.secretVolume(secretName: 'maven-settings', mountPath: "${workingdir}/.m2")
          ]
       ){
            body ()
       }
  }

}
