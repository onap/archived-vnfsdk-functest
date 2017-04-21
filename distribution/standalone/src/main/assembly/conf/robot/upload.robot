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
    SSHLibrary.Put Directory    ${SCRIPT_DIR}    ${DIR_REMOTE}    0777    CRLF    recursive=True
   


