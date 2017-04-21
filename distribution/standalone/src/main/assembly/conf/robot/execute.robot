*** Settings ***
Suite Teardown    Close All Connections
Library           String
Library           OperatingSystem
Library           SSHLibrary
Library           BuiltIn

*** Test Cases ***
Copy Directory from Local to Remote
    Open Connection    ${NODE_IP}
    Login    ${NODE_USERNAME}    ${NODE_PASSWORD}
    ${CommandToExecute} =    Execute Command    ${REMOTE_COMMAND}
    SSHLibrary.Get Directory    ${DIR_REMOTE_RESULT}    ${DIR_RESULT}    recursive=True
    Close All Connections
   


