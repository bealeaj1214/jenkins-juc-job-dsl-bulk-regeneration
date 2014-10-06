package jobdsl;

import javaposse.jobdsl.dsl.Job


public class JobUtilities {


    static def setupGradleGerritJobName(Job job,String projectName){

        def matcher = projectName =~/[\/]+/
        def jobName = "${matcher.replaceAll('-')}-gradle-gerrit"

        job.with {
            name "${jobName}"
        }

    }

    static def buildChooserAttrs = [
        class: 'com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTriggerBuildChooser'
    ]

    static def setupGerritScm(
        Job job,
        String projectName,
        baseGerritURL='ssh://jenkins@gerritServer:29418'){

        def bldChooseNode =
            'hudson.plugins.git.extensions.impl.BuildChooserSetting'

        job.with {
            scm {
                git(
                    "${baseGerritURL}/${projectName}",
                    'origin/$BRANCH_REFSPEC')  { node ->
                        node/extensions {
                            "${bldChooseNode}"{
                                buildChooser(buildChooserAttrs){
                                    separator('#')
                                }
                            }
                        }

                        def remoteConfig =
                            node/userRemoteConfigs/'hudson.plugins.git.UserRemoteConfig'
                        remoteConfig << refspec('$GERRIT_REFSPEC')
                }
            }
        }

    }

    static def setupGerritTrigger(
        Job job,
        String projectName){

        job.with{

            triggers {
                gerrit {
                    events {
                        PatchsetCreated
                        DraftPublished
                    }
                    project(
                        "plain: ${projectName}",
                        'ant:**'
                    )
                }
            }
        }

    }

    static def setupGradleBuild(Job job) {

        job.with{

            steps {
                gradle (
                    "build",
                    null,
                    true
                ){ node -> //hudson.plugins.gradle.Gradle
                    node/useWorkspaceAsHome(true)
                }
            }
        }
    }
}
