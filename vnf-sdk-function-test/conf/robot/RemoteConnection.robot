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
    SSHLibrary.Put Directory    ${SCRIPT_DIR}    ${DIR_REMOTE}    0744    CRLF    recursive=True
    ${CommandToExecute} =    Execute Command    ${REMOTE_COMMAND}

Copy Directory from Remote to Local
    SSHLibrary.Get Directory    ${DIR_REMOTE_RESULT}    ${DIR_RESULT}    recursive=True
    Close All Connections
