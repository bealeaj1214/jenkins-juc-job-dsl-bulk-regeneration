import jenkins.model.*

import hudson.model.Cause
import hudson.model.ParametersAction
import hudson.model.StringParameterValue


def jenkins = jenkins.model.Jenkins.instance

def jobGeneratorName='gradle-gerrit-job-generator'

def jobGenerator = jenkins.getItem(jobGeneratorName)

def projectParamName ='PROJECT_NAME'
def projectParamValue='sample/java/library'

if(jobGenerator){

    boolean buildQueued =
        jobGenerator.scheduleBuild(
            5,
            new Cause.UserIdCause(),
            new ParametersAction(
                new StringParameterValue(
                    projectParamName,
                    projectParamValue)
            )

        )

}