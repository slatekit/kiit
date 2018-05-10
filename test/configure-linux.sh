#!/bin/bash

#trapping Control + C
#these statements must be the first statements in the script to trap the CTRL C event

trap ctrl_c INT

function ctrl_c() {
  logMsgToConfigSysLog "INFO" "INFO: Aborting the script."
  exit 1
}

##########  Variable Declarations - Start  ##########

#name of the current script. This will get overwritten by the child script which calls this
SCRIPT_NAME=configure-linux.sh
#version of the current script. This will get overwritten by the child script which calls this
SCRIPT_VERSION=1.21

#application tag. This will get overwritten by the child script which calls this
APP_TAG=

#directory location for syslog
RSYSLOG_ETCDIR_CONF=/etc/rsyslog.d
#name and location of loggly syslog file
LOGGLY_RSYSLOG_CONFFILE=$RSYSLOG_ETCDIR_CONF/22-loggly.conf
#name and location of loggly syslog backup file
LOGGLY_RSYSLOG_CONFFILE_BACKUP=$LOGGLY_RSYSLOG_CONFFILE.loggly.bk

#syslog directory
RSYSLOG_DIR=/var/spool/rsyslog
#rsyslog service name
RSYSLOG_SERVICE=rsyslog
#syslog-ng
SYSLOG_NG_SERVICE=syslog-ng
#rsyslogd
RSYSLOGD=rsyslogd
#minimum version of rsyslog to enable logging to loggly
MIN_RSYSLOG_VERSION=5.8.0
#this variable will hold the users syslog version
RSYSLOG_VERSION=

#this variable will hold the existing syslog port of 22-loggly.conf
EXISTING_SYSLOG_PORT=

#this variable will hold the host name
HOST_NAME=
#this variable will hold the name of the linux distribution
LINUX_DIST=

#host name for logs-01.loggly.com
LOGS_01_HOST=logs-01.loggly.com
LOGS_01_URL=https://$LOGS_01_HOST
#this variable will contain loggly account url in the format https://$LOGGLY_ACCOUNT.loggly.com
LOGGLY_ACCOUNT_URL=
#loggly.com URL
LOGGLY_COM_URL=https://www.loggly.com

######Inputs provided by user######
#this variable will hold the loggly account name provided by user.
#this is a mandatory input
LOGGLY_ACCOUNT=
#this variable will hold the loggly authentication token provided by user.
#this is a mandatory input
LOGGLY_AUTH_TOKEN=
#this variable will identify if the user has selected to rollback settings
LOGGLY_ROLLBACK=
#this variable will hold the user name provided by user
#this is a mandatory input
LOGGLY_USERNAME=
#this variable will hold the password provided by user
#this is a mandatory input
LOGGLY_PASSWORD=

#if this variable is set to true then suppress all prompts
SUPPRESS_PROMPT="false"

#variables used in 22-loggly.conf file
LOGGLY_SYSLOG_PORT=6514
LOGGLY_DISTRIBUTION_ID="41058"

#Instruction link on how to configure loggly on linux manually. This will get overwritten by the child script which calls this
#on how to configure the child application
MANUAL_CONFIG_INSTRUCTION="Manual instructions to configure rsyslog on Linux are available at https://www.loggly.com/docs/rsyslog-tls-configuration/. Rsyslog troubleshooting instructions are available at https://www.loggly.com/docs/troubleshooting-rsyslog/"

#this variable is set if the script is invoked via some other calling script
IS_INVOKED=

#this variable will hold if the check env function for linux is invoked
LINUX_ENV_VALIDATED="false"

#this variable will inform if verification needs to be performed
LINUX_DO_VERIFICATION="true"

#this variable will enable sending logs over TLS
LOGGLY_TLS_SENDING="true"

#Setting FORCE_SECURE to false
FORCE_SECURE="false"

#Setting LOGGLY_REMOVE to false
LOGGLY_REMOVE="false"

#Setting INSECURE mode to false initially
INSECURE_MODE="false"

#Setting invalid subdomain value
INVALID_SUBDOMAIN=*".loggly.com"*

##########  Variable Declarations - End  ##########

#check if the Linux environment is compatible with Loggly.
#Also set few variables after the check.
checkLinuxLogglyCompatibility() {
  #check if the user has root permission to run this script
  checkIfUserHasRootPrivileges

  #check if the OS is supported by the script. If no, then exit
  checkIfSupportedOS

  #check if required dependencies to run the script are not installed. If yes, ask user to install them manually and run the script again.
  checkIfRequiredDependenciesAreNotInstalled

  #check if package-manager is present on the machine
  checkIfPackageManagerIsPresent

  #set the basic variables needed by this script
  setLinuxVariables

  #check if the Loggly servers are accessible. If no, ask user to check network connectivity & exit
  checkIfLogglyServersAccessible

  #check if user credentials are valid. If no, then exit
  checkIfValidUserNamePassword

  #get authentication token if not provided
  getAuthToken

  #check if authentication token is valid. If no, then exit.
  checkIfValidAuthToken

  #checking if syslog-ng is configured as a service
  checkifSyslogNgConfiguredAsService

  #check if systemd is present in machine.
  checkIfSystemdConfigured

  #check if rsyslog is configured as service. If no, then exit
  checkIfRsyslogConfiguredAsService

  #check if multiple rsyslog are present in the system. If yes, then exit
  checkIfMultipleRsyslogConfigured

  #check for the minimum version of rsyslog i.e 5.8.0. If no, then exit
  checkIfMinVersionOfRsyslog

  #check if selinux service is enforced. if yes, ask the user to manually disable and exit the script
  checkIfSelinuxServiceEnforced

  #update rsyslog.conf and adds $MaxMessageSize in it
  modifyMaxMessageSize

  LINUX_ENV_VALIDATED="true"
}

# executing the script for loggly to install and configure rsyslog.
installLogglyConf() {
  #log message indicating starting of Loggly configuration
  logMsgToConfigSysLog "INFO" "INFO: Initiating Configure Loggly for Linux."

  if [ "$LINUX_ENV_VALIDATED" = "false" ]; then
    checkLinuxLogglyCompatibility
  fi

  #create rsyslog dir if it doesn't exist, Modify the permission on rsyslog directory if exist on Ubuntu
  createRsyslogDir

  #if all the above check passes, write the 22-loggly.conf file
  checkAuthTokenAndWriteContents

  if [ "$LINUX_DO_VERIFICATION" = "true" ]; then
    #check if the logs are going to loggly fro linux system now
    checkIfLogsMadeToLoggly
  fi

  if [ "$IS_INVOKED" = "" ]; then
    logMsgToConfigSysLog "SUCCESS" "SUCCESS: Linux system successfully configured to send logs via Loggly."
  fi

}

#remove loggly configuration from Linux system
removeLogglyConf() {
  #log message indicating starting of Loggly configuration
  logMsgToConfigSysLog "INFO" "INFO: Initiating uninstall Loggly for Linux."

  #check if the user has root permission to run this script
  checkIfUserHasRootPrivileges

  #check if the OS is supported by the script. If no, then exit
  checkIfSupportedOS

  #set the basic variables needed by this script
  setLinuxVariables

  #remove systemd-rsyslog configuration
  revertSystemdChanges

  #remove 22-loggly.conf file
  remove22LogglyConfFile

  #restart rsyslog service
  restartRsyslog

  #log success message
  logMsgToConfigSysLog "SUCCESS" "SUCCESS: Uninstalled Loggly configuration from Linux system."
}

#checks if user has root privileges
checkIfUserHasRootPrivileges() {
  #This script needs to be run as root
  if [[ $EUID -ne 0 ]]; then
    logMsgToConfigSysLog "ERROR" "ERROR: This script must be run as root."
    exit 1
  fi
}

#check if package-manager is present on the machine
checkIfPackageManagerIsPresent() {
  if [ -x "$(command -v apt-get)" ]; then
    PKG_MGR="apt-get"
  else
    if [ -x "$(command -v yum)" ]; then
      PKG_MGR="yum"
    fi
  fi
}

#check if required dependencies to run the script are not installed, If yes then ask user to install them manually and run the script again
checkIfRequiredDependenciesAreNotInstalled() {
  if ! [ -x "$(command -v curl)" ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: 'Curl' executable could not be found on your machine, since it is a dependent package to run this script, please install it manually and then run the script again."
    exit 1
  elif ! [ -x "$(command -v ping)" ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: 'Ping' executable could not be found on your machine, since it is a dependent package to run this script, please install it manually and then run the script again."
    exit 1
  fi
}

#check if supported operating system
checkIfSupportedOS() {
  getOs

  LINUX_DIST_IN_LOWER_CASE=$(echo $LINUX_DIST | tr "[:upper:]" "[:lower:]")

  case "$LINUX_DIST_IN_LOWER_CASE" in
  *"ubuntu"*)
    echo "INFO: Operating system is Ubuntu."
    ;;
  *"red"*)
    echo "INFO: Operating system is Red Hat."
    ;;
  *"centos"*)
    echo "INFO: Operating system is CentOS."
    ;;
  *"debian"*)
    echo "INFO: Operating system is Debian."
    ;;
  *"amazon"*)
    echo "INFO: Operating system is Amazon AMI."
    ;;
  *"darwin"*)
    #if the OS is mac then exit
    logMsgToConfigSysLog "ERROR" "ERROR: This script is for Linux systems, and Darwin or Mac OSX are not currently supported. You can find alternative options here: https://www.loggly.com/docs/send-mac-logs-to-loggly/"
    exit 1
    ;;
  *)
    logMsgToConfigSysLog "WARN" "WARN: The linux distribution '$LINUX_DIST' has not been previously tested with Loggly."
    if [ "$SUPPRESS_PROMPT" == "false" ]; then
      while true; do
        read -p "Would you like to continue anyway? (yes/no)" yn
        case $yn in
        [Yy]*)
          break
          ;;
        [Nn]*)
          exit 1
          ;;
        *) echo "Please answer yes or no." ;;
        esac
      done
    fi
    ;;
  esac
}

getOs() {
  # Determine OS platform
  UNAME=$(uname | tr "[:upper:]" "[:lower:]")
  # If Linux, try to determine specific distribution
  if [ "$UNAME" == "linux" ]; then
    # If available, use LSB to identify distribution
    if [ -f /etc/lsb-release -o -d /etc/lsb-release.d ]; then
      LINUX_DIST=$(lsb_release -i | cut -d: -f2 | sed s/'^\t'//)
      # If system-release is available, then try to identify the name
    elif [ -f /etc/system-release ]; then
      LINUX_DIST=$(cat /etc/system-release | cut -f 1 -d " ")
      # Otherwise, use release info file
    else
      LINUX_DIST=$(ls -d /etc/[A-Za-z]*[_-][rv]e[lr]* | grep -v "lsb" | cut -d'/' -f3 | cut -d'-' -f1 | cut -d'_' -f1)
    fi
  fi

  # For everything else (or if above failed), just use generic identifier
  if [ "$LINUX_DIST" == "" ]; then
    LINUX_DIST=$(uname)
  fi
}

#sets linux variables which will be used across various functions
setLinuxVariables() {
  #set host name
  HOST_NAME=$(hostname)

  #set loggly account url
  LOGGLY_ACCOUNT_URL=https://$LOGGLY_ACCOUNT.loggly.com
}

#checks if all the various endpoints used for configuring loggly are accessible
checkIfLogglyServersAccessible() {
  echo "INFO: Checking if $LOGS_01_HOST can be pinged."
  if [ $(ping -c 1 $LOGS_01_HOST | grep "1 packets transmitted, 1 received, 0% packet loss" | wc -l) == 1 ]; then
    echo "INFO: $LOGS_01_HOST can be pinged."
  else
    logMsgToConfigSysLog "WARNING" "WARNING: $LOGS_01_HOST cannot be pinged. Please check your network and firewall settings."
  fi

  echo "INFO: Checking if $LOGS_01_HOST is reachable."
  (</dev/tcp/$LOGS_01_HOST/$LOGGLY_SYSLOG_PORT) >/dev/null 2>&1
  if [ $? -eq 0 ]; then
    echo "INFO: $LOGS_01_HOST is reachable."
  else
    logMsgToConfigSysLog "ERROR" "ERROR: $LOGS_01_HOST is not reachable. Please check your network and firewall settings."
    exit 1
  fi

  echo "INFO: Checking if $LOGS_01_HOST is reachable via $LOGGLY_SYSLOG_PORT port. This may take some time."
  if [ $(curl --connect-timeout 10 $LOGS_01_HOST:$LOGGLY_SYSLOG_PORT 2>&1 | grep "Empty reply from server" | wc -l) == 1 ]; then
    echo "INFO: $LOGS_01_HOST is reachable via $LOGGLY_SYSLOG_PORT port."
  else
    logMsgToConfigSysLog "ERROR" "ERROR: $LOGS_01_HOST is not reachable via $LOGGLY_SYSLOG_PORT port. Please check your network and firewall settings."
    exit 1
  fi

  echo "INFO: Checking if '$LOGGLY_ACCOUNT' subdomain is valid."
  if [[ $LOGGLY_ACCOUNT != $INVALID_SUBDOMAIN ]]; then
      if [[ $(curl --head -s --request GET $LOGGLY_ACCOUNT_URL/login | grep "200 OK\|HTTP/2 200") ]]; then
          echo "INFO: $LOGGLY_ACCOUNT_URL is valid and reachable."
      else
          logMsgToConfigSysLog "ERROR" "ERROR: This is not a recognized subdomain. Please ask the account owner for the subdomain they signed up with."
          exit 1
      fi
  else
    logMsgToConfigSysLog "ERROR" "ERROR: This is not a recognized subdomain. Please ask the account owner for the subdomain they signed up with. Please note that your subdomain is just the first string in your loggly account URL not the entire account name."
    exit 1
  fi

  echo "INFO: Checking if Gen2 account."
  if [ $(curl -s --head --request GET $LOGGLY_ACCOUNT_URL/apiv2/customer | grep "404 NOT FOUND" | wc -l) == 1 ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: This scripts need a Gen2 account. Please contact Loggly support."
    exit 1
  else
    echo "INFO: It is a Gen2 account."
  fi
}

#check if user name and password is valid
checkIfValidUserNamePassword() {
  echo "INFO: Checking if provided username and password is correct."
  if [ $(curl -s -u $LOGGLY_USERNAME:$LOGGLY_PASSWORD $LOGGLY_ACCOUNT_URL/apiv2/customer | grep "Unauthorized" | wc -l) == 1 ]; then
    logMsgToConfigSysLog "INFO" "INFO: Please check your username or reset your password at $LOGGLY_ACCOUNT_URL/account/users/"
    logMsgToConfigSysLog "ERROR" "ERROR: Invalid Loggly username or password. Your username is visible at the top right of the Loggly console. You can reset your password at http://<subdomain>.loggly.com/login."
    exit 1
  else
    logMsgToConfigSysLog "INFO" "INFO: Username and password authorized successfully."
  fi
}

getAuthToken() {
  if [ "$LOGGLY_AUTH_TOKEN" = "" ]; then
    logMsgToConfigSysLog "INFO" "INFO: Authentication token not provided. Trying to retrieve it from $LOGGLY_ACCOUNT_URL account."
    #get authentication token if user has not provided one
    tokenstr=$(curl -s -u $LOGGLY_USERNAME:$LOGGLY_PASSWORD $LOGGLY_ACCOUNT_URL/apiv2/customer | grep -v "token")

    #get the string from index 0 to first occurence of ,
    tokenstr=${tokenstr%%,*}

    #get the string from index 0 to last occurence of "
    tokenstr=${tokenstr%\"*}

    #get the string from first occurence of " to the end
    tokenstr=${tokenstr#*\"}

    LOGGLY_AUTH_TOKEN=$tokenstr

    logMsgToConfigSysLog "INFO" "INFO: Retrieved authentication token: $LOGGLY_AUTH_TOKEN"
  fi
}

#check if authentication token is valid
checkIfValidAuthToken() {
  echo "INFO: Checking if provided auth token is correct."
  if [ $(curl -s -u $LOGGLY_USERNAME:$LOGGLY_PASSWORD $LOGGLY_ACCOUNT_URL/apiv2/customer | grep \"$LOGGLY_AUTH_TOKEN\" | wc -l) == 1 ]; then
    logMsgToConfigSysLog "INFO" "INFO: Authentication token validated successfully."
  else
    logMsgToConfigSysLog "ERROR" "ERROR: Invalid authentication token $LOGGLY_AUTH_TOKEN. You can get valid authentication token by following instructions at https://www.loggly.com/docs/customer-token-authentication-token/."
    exit 1
  fi
}

#check if rsyslog is configured as service. If it is configured as service and not started, start the service
checkIfRsyslogConfiguredAsService() {
  if [ -f /etc/init.d/$RSYSLOG_SERVICE ]; then
    logMsgToConfigSysLog "INFO" "INFO: $RSYSLOG_SERVICE is present as service."
  elif [ -f /usr/lib/systemd/system/$RSYSLOG_SERVICE.service ]; then
    logMsgToConfigSysLog "INFO" "INFO: $RSYSLOG_SERVICE is present as service."
  else
    logMsgToConfigSysLog "ERROR" "ERROR: $RSYSLOG_SERVICE is not present as service."
    exit 1
  fi

  #checking if syslog-ng is running as a service
  checkifSyslogNgConfiguredAsService

  if [ $(ps -A | grep "$RSYSLOG_SERVICE" | wc -l) -eq 0 ]; then
    logMsgToConfigSysLog "INFO" "INFO: $RSYSLOG_SERVICE is not running. Attempting to start service."
    service $RSYSLOG_SERVICE start
  fi
}

checkifSyslogNgConfiguredAsService() {
  if [ $(ps -A | grep "$SYSLOG_NG_SERVICE" | wc -l) -gt 0 ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: This script does not currently support syslog-ng. Please follow the instructions on this page https://www.loggly.com/docs/syslog-ng-manual-configuration"
    exit 1
  fi
}

#check if systemd is present in machine.
checkIfSystemdConfigured() {
  FILE="/etc/systemd/journald.conf"
  if [ -f "$FILE" ]; then
    logMsgToConfigSysLog "INFO" "INFO: Systemd is present. Configuring logs from Systemd to rsyslog."
    cp /etc/systemd/journald.conf /etc/systemd/journald.conf.loggly.bk
    sed -i 's/.*ForwardToSyslog.*/ForwardToSyslog=Yes/g' /etc/systemd/journald.conf
    logMsgToConfigSysLog "INFO" "INFO: Restarting Systemd-journald"
    systemctl restart systemd-journald
  fi
}

#check if multiple versions of rsyslog is configured
checkIfMultipleRsyslogConfigured() {
  if [ $(ps -A | grep "$RSYSLOG_SERVICE" | wc -l) -gt 1 ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: Multiple (more than 1) $RSYSLOG_SERVICE is running."
    exit 1
  fi
}

#check if minimum version of rsyslog required to configure loggly is met
checkIfMinVersionOfRsyslog() {
  RSYSLOG_VERSION=$($RSYSLOGD -version | grep "$RSYSLOGD")
  RSYSLOG_VERSION=${RSYSLOG_VERSION#* }
  RSYSLOG_VERSION=${RSYSLOG_VERSION%,*}
  RSYSLOG_VERSION=$RSYSLOG_VERSION | tr -d " "
  if [ $(compareVersions $RSYSLOG_VERSION $MIN_RSYSLOG_VERSION 3) -lt 0 ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: Minimum rsyslog version required to run this script is 5.8.0. Please upgrade your rsyslog version or follow the manual instructions."
    exit 1
  fi
}

#check if SeLinux service is enforced
checkIfSelinuxServiceEnforced() {
  isSelinuxInstalled=$(getenforce -ds 2>/dev/null)
  if [ $? -ne 0 ]; then
    logMsgToConfigSysLog "INFO" "INFO: selinux status is not enforced."
  elif [ $(getenforce | grep "Enforcing" | wc -l) -gt 0 ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: selinux status is 'Enforcing'. Please manually restart the rsyslog daemon or turn off selinux by running 'setenforce 0' and then rerun the script."
    exit 1
  fi
}

#update rsyslog.conf and adds $MaxMessageSize in it
modifyMaxMessageSize() {
  if grep -q '$MaxMessageSize' "/etc/rsyslog.conf"; then
    sed -i 's/.*$MaxMessageSize.*/$MaxMessageSize 64k/g' /etc/rsyslog.conf
  else
    sed -i '1 a $MaxMessageSize 64k' /etc/rsyslog.conf
  fi
  logMsgToConfigSysLog "INFO" "INFO: Modified \$MaxMessageSize to 64k in rsyslog.conf"
}

#check if authentication token is valid and then write contents to 22-loggly.conf file to /etc/rsyslog.d directory
checkAuthTokenAndWriteContents() {
  if [ "$LOGGLY_AUTH_TOKEN" != "" ]; then
    writeContents $LOGGLY_ACCOUNT $LOGGLY_AUTH_TOKEN $LOGGLY_DISTRIBUTION_ID $LOGS_01_HOST $LOGGLY_SYSLOG_PORT
    restartRsyslog
  else
    logMsgToConfigSysLog "ERROR" "ERROR: Loggly auth token is required to configure rsyslog. Please pass -a <auth token> while running script."
    exit 1
  fi
}

downloadTlsCerts() {
  echo "DOWNLOADING CERTIFICATE"
  mkdir -pv /etc/rsyslog.d/keys/ca.d
  curl -O https://logdog.loggly.com/media/logs-01.loggly.com_sha12.crt
  sudo cp -Prf logs-01.loggly.com_sha12.crt /etc/rsyslog.d/keys/ca.d/logs-01.loggly.com_sha12.crt
  sudo rm logs-01.loggly.com_sha12.crt
  if [ ! -f /etc/rsyslog.d/keys/ca.d//logs-01.loggly.com_sha12.crt ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: Certificate could not be downloaded."
    exit 1
  fi
}

confString() {
  RSYSLOG_VERSION_TMP=$(echo $RSYSLOG_VERSION | cut -d "." -f1)
  inputStr_TLS_RSYS_7="
#          -------------------------------------------------------
#          Syslog Logging Directives for Loggly ($LOGGLY_ACCOUNT.loggly.com)
#          -------------------------------------------------------
##########################################################
### RsyslogTemplate for Loggly ###
##########################################################

\$template LogglyFormat,\"<%pri%>%protocol-version% %timestamp:::date-rfc3339% %HOSTNAME% %app-name% %procid% %msgid% [$LOGGLY_AUTH_TOKEN@$LOGGLY_DISTRIBUTION_ID tag=\\\"RsyslogTLS\\\"] %msg%\n\"

# Setup disk assisted queues
\$WorkDirectory /var/spool/rsyslog # where to place spool files
\$ActionQueueFileName fwdRule1     # unique name prefix for spool files
\$ActionQueueMaxDiskSpace 1g       # 1gb space limit (use as much as possible)
\$ActionQueueSaveOnShutdown on     # save messages to disk on shutdown
\$ActionQueueType LinkedList       # run asynchronously
\$ActionResumeRetryCount -1        # infinite retries if host is down

#RsyslogGnuTLS
\$DefaultNetstreamDriverCAFile /etc/rsyslog.d/keys/ca.d/logs-01.loggly.com_sha12.crt
\$ActionSendStreamDriver gtls
\$ActionSendStreamDriverMode 1
\$ActionSendStreamDriverAuthMode x509/name
\$ActionSendStreamDriverPermittedPeer *.loggly.com

*.* @@$LOGS_01_HOST:$LOGGLY_SYSLOG_PORT;LogglyFormat
#################END CONFIG FILE#########################
  "
  inputStr_TLS_RSYS_8="
#          -------------------------------------------------------
#          Syslog Logging Directives for Loggly ($LOGGLY_ACCOUNT.loggly.com)
#          -------------------------------------------------------
# Setup disk assisted queues
\$WorkDirectory /var/spool/rsyslog # where to place spool files
\$ActionQueueFileName fwdRule1     # unique name prefix for spool files
\$ActionQueueMaxDiskSpace 1g       # 1gb space limit (use as much as possible)
\$ActionQueueSaveOnShutdown on     # save messages to disk on shutdown
\$ActionQueueType LinkedList       # run asynchronously
\$ActionResumeRetryCount -1        # infinite retries if host is down

#RsyslogGnuTLS
\$DefaultNetstreamDriverCAFile /etc/rsyslog.d/keys/ca.d/logs-01.loggly.com_sha12.crt


template(name=\"LogglyFormat\" type=\"string\"
string=\"<%pri%>%protocol-version% %timestamp:::date-rfc3339% %HOSTNAME% %app-name% %procid% %msgid% [$LOGGLY_AUTH_TOKEN@$LOGGLY_DISTRIBUTION_ID tag=\\\"RsyslogTLS\\\"] %msg%\n\"
)

# Send messages to Loggly over TCP using the template.
action(type=\"omfwd\" protocol=\"tcp\" target=\"$LOGS_01_HOST\" port=\"$LOGGLY_SYSLOG_PORT\" template=\"LogglyFormat\" StreamDriver=\"gtls\" StreamDriverMode=\"1\" StreamDriverAuthMode=\"x509/name\" StreamDriverPermittedPeers=\"*.loggly.com\")
  "

  inputStr_NO_TLS="
#          -------------------------------------------------------
#          Syslog Logging Directives for Loggly ($LOGGLY_ACCOUNT.loggly.com)
#          -------------------------------------------------------
# Define the template used for sending logs to Loggly. Do not change this format.
\$template LogglyFormat,\"<%pri%>%protocol-version% %timestamp:::date-rfc3339% %HOSTNAME% %app-name% %procid% %msgid% [$LOGGLY_AUTH_TOKEN@$LOGGLY_DISTRIBUTION_ID tag=\\\"Rsyslog\\\"] %msg%\n\"

\$WorkDirectory /var/spool/rsyslog # where to place spool files
\$ActionQueueFileName fwdRule1 # unique name prefix for spool files
\$ActionQueueMaxDiskSpace 1g   # 1gb space limit (use as much as possible)
\$ActionQueueSaveOnShutdown on # save messages to disk on shutdown
\$ActionQueueType LinkedList   # run asynchronously
\$ActionResumeRetryCount -1    # infinite retries if host is down

# Send messages to Loggly over TCP using the template.
*.*             @@$LOGS_01_HOST:$LOGGLY_SYSLOG_PORT;LogglyFormat
#     -------------------------------------------------------
  "
  if [ "$RSYSLOG_VERSION_TMP" -le "7" ]; then
    inputStrTls=$inputStr_TLS_RSYS_7
  elif [ "$RSYSLOG_VERSION_TMP" -ge "8" ]; then
    inputStrTls=$inputStr_TLS_RSYS_8
  fi
  inputStr=$inputStr_NO_TLS
}

#install the certificate and check if gnutls package is installed
installTLSDependencies() {
  if [ $LOGGLY_TLS_SENDING == "true" ]; then
    downloadTlsCerts
    if [ "$SUPPRESS_PROMPT" == "true" ]; then
      /bin/bash -c "sudo $PKG_MGR install -y rsyslog-gnutls"
    else
      /bin/bash -c "sudo $PKG_MGR install rsyslog-gnutls"
    fi
    if [ "$PKG_MGR" == "yum" ]; then
      if [ $(rpm -qa | grep -c "rsyslog-gnutls") -eq 0 ]; then
        DEPENDENCIES_INSTALLED="false"
        if [ "$FORCE_SECURE" == "true" ]; then
          logMsgToConfigSysLog "WARN" "WARN: The rsyslog-gnutls package could not be download automatically because your package manager could not be found. Please install it and restart the rsyslog service to send logs to Loggly."
        fi
      fi
    elif [ "$PKG_MGR" == "apt-get" ]; then
      if [ $(dpkg-query -W -f='${Status}' rsyslog-gnutls 2>/dev/null | grep -c "ok installed") -eq 0 ]; then
        DEPENDENCIES_INSTALLED="false"
        if [ "$FORCE_SECURE" == "true" ]; then
          logMsgToConfigSysLog "WARN" "WARN: The rsyslog-gnutls package could not be download automatically because your package manager could not be found. Please install it and restart the rsyslog service to send logs to Loggly."
        fi
      fi
    else
      DEPENDENCIES_INSTALLED="false"
    fi
    inputStr=$inputStrTls
  fi
}

#prompt users if they want to switch to insecure mode on gnutls-package download failure
switchToInsecureModeIfTLSNotFound() {
  if [ "$FORCE_SECURE" == "false" ]; then
    if [ "$DEPENDENCIES_INSTALLED" == "false" ]; then
      if [ "$SUPPRESS_PROMPT" == "false" ]; then
        logMsgToConfigSysLog "WARN" "WARN: The rsyslog-gnutls package could not download automatically either because of your package manager could not be found or due to some other reason."
        while true; do
          read -p "Do you wish to continue with insecure mode? (yes/no)" yn
          case $yn in
          [Yy]*)
            logMsgToConfigSysLog "INFO" "INFO: Going to overwrite the conf file: $LOGGLY_RSYSLOG_CONFFILE with insecure configuration"
            LOGGLY_SYSLOG_PORT=514
            break
            ;;
          [Nn]*)
            logMsgToConfigSysLog "INFO" "INFO: Since the rsyslog-gnutls package could not be installed automatically, please install it yourself and then re-run the script using the --force-secure flag. This option will force the secure TLS configuration instead of falling back on insecure mode. It is useful for Linux distributions where this script cannot automatically detect the dependency using yum or apt-get."
            exit 1
            ;;
          *) echo "Please answer yes or no." ;;
          esac
        done
      else
        logMsgToConfigSysLog "WARN" "WARN: The rsyslog-gnutls package could not download automatically either because of your package manager could not be found or due to some other reason, continuing with insecure mode."
        LOGGLY_SYSLOG_PORT=514

      fi
      confString
    fi
  fi
}

#write the contents to 22-loggly.conf file
writeContents() {
  confString
  checkScriptRunningMode
  installTLSDependencies
  switchToInsecureModeIfTLSNotFound
  WRITE_SCRIPT_CONTENTS="false"

  if [ -f "$LOGGLY_RSYSLOG_CONFFILE" ]; then
    logMsgToConfigSysLog "INFO" "INFO: Loggly rsyslog file $LOGGLY_RSYSLOG_CONFFILE already exist."

    STR_SIZE=${#inputStr}
    SIZE_FILE=$(stat -c%s "$LOGGLY_RSYSLOG_CONFFILE")

    #actual file size and variable size with same contents always differ in size with one byte
    STR_SIZE=$((STR_SIZE + 1))

    if [ "$STR_SIZE" -ne "$SIZE_FILE" ]; then

      logMsgToConfigSysLog "WARN" "WARN: Loggly rsyslog file /etc/rsyslog.d/22-loggly.conf content has changed."
      if [ "$SUPPRESS_PROMPT" == "false" ]; then
        while true; do
          read -p "Do you wish to override $LOGGLY_RSYSLOG_CONFFILE and re-verify configuration? (yes/no)" yn
          case $yn in
          [Yy]*)
            logMsgToConfigSysLog "INFO" "INFO: Going to back up the conf file: $LOGGLY_RSYSLOG_CONFFILE to $LOGGLY_RSYSLOG_CONFFILE_BACKUP"
            mv -f $LOGGLY_RSYSLOG_CONFFILE $LOGGLY_RSYSLOG_CONFFILE_BACKUP
            WRITE_SCRIPT_CONTENTS="true"
            break
            ;;
          [Nn]*)
            LINUX_DO_VERIFICATION="false"
            logMsgToConfigSysLog "INFO" "INFO: Skipping Linux verification."
            break
            ;;
          *) echo "Please answer yes or no." ;;
          esac
        done
      else
        logMsgToConfigSysLog "INFO" "INFO: Going to back up the conf file: $LOGGLY_RSYSLOG_CONFFILE to $LOGGLY_RSYSLOG_CONFFILE_BACKUP"
        mv -f $LOGGLY_RSYSLOG_CONFFILE $LOGGLY_RSYSLOG_CONFFILE_BACKUP
        WRITE_SCRIPT_CONTENTS="true"
      fi
    else
      LINUX_DO_VERIFICATION="false"
    fi
  else
    WRITE_SCRIPT_CONTENTS="true"
  fi

  if [ "$WRITE_SCRIPT_CONTENTS" == "true" ]; then

    cat <<EOIPFW >>$LOGGLY_RSYSLOG_CONFFILE
$inputStr
EOIPFW

  fi

}

#create /var/spool/rsyslog directory if not already present. Modify the permission of this directory for Ubuntu
createRsyslogDir() {
  if [ -d "$RSYSLOG_DIR" ]; then
    logMsgToConfigSysLog "INFO" "INFO: $RSYSLOG_DIR already exist, so not creating directory."
    if [[ "$LINUX_DIST" == *"Ubuntu"* ]]; then
      logMsgToConfigSysLog "INFO" "INFO: Changing the permission on the rsyslog in /var/spool"
      chown -R syslog:adm $RSYSLOG_DIR
    fi
  else
    logMsgToConfigSysLog "INFO" "INFO: Creating directory $SYSLOGDIR"
    mkdir -v $RSYSLOG_DIR
    if [[ "$LINUX_DIST" == *"Ubuntu"* ]]; then
      chown -R syslog:adm $RSYSLOG_DIR
    fi
  fi
}

#check if the logs made it to Loggly
checkIfLogsMadeToLoggly() {
  logMsgToConfigSysLog "INFO" "INFO: Sending test message to Loggly."
  uuid=$(cat /proc/sys/kernel/random/uuid)

  queryParam="syslog.appName%3ALOGGLYVERIFY%20$uuid"
  logger -t "LOGGLYVERIFY" "LOGGLYVERIFY-Test message for verification with UUID $uuid"

  counter=1
  maxCounter=10
  finalCount=0

  queryUrl="$LOGGLY_ACCOUNT_URL/apiv2/search?q=$queryParam"
  logMsgToConfigSysLog "INFO" "INFO: Search URL: $queryUrl"

  logMsgToConfigSysLog "INFO" "INFO: Verifying if the log made it to Loggly."
  logMsgToConfigSysLog "INFO" "INFO: Verification # $counter of total $maxCounter."
  searchAndFetch finalCount "$queryUrl"
  let counter=$counter+1

  while [ "$finalCount" -eq 0 ]; do
    echo "INFO: Did not find the test log message in Loggly's search yet. Waiting for 30 secs."
    sleep 30
    echo "INFO: Done waiting. Verifying again."
    logMsgToConfigSysLog "INFO" "INFO: Verification # $counter of total $maxCounter."
    searchAndFetch finalCount "$queryUrl"
    let counter=$counter+1
    if [ "$counter" -gt "$maxCounter" ]; then
      logMsgToConfigSysLog "ERROR" "ERROR: Logs did not make to Loggly in time. Please check network and firewall settings and retry."
      exit 1
    fi
  done

  if [ "$finalCount" -eq 1 ]; then
    if [ "$IS_INVOKED" = "" ]; then
      logMsgToConfigSysLog "SUCCESS" "SUCCESS: Verification logs successfully transferred to Loggly! You are now sending Linux system logs to Loggly."
      exit 0
    else
      logMsgToConfigSysLog "INFO" "SUCCESS: Verification logs successfully transferred to Loggly! You are now sending Linux system logs to Loggly."
    fi
  fi

}

#delete 22-loggly.conf file
remove22LogglyConfFile() {
  if [ -f "$LOGGLY_RSYSLOG_CONFFILE" ]; then
    rm -rf "$LOGGLY_RSYSLOG_CONFFILE"
  fi
}

revertSystemdChanges() {
  FILE="/etc/systemd/journald.conf.loggly.bk"
  if [ -f "$FILE" ]; then
    cp /etc/systemd/journald.conf.loggly.bk /etc/systemd/journald.conf
    rm /etc/systemd/journald.conf.loggly.bk
    logMsgToConfigSysLog "INFO" "INFO: Reverted Systemd-rsyslog configuration"
    systemctl restart systemd-journald
  fi
}

#compares two version numbers, used for comparing versions of various softwares
compareVersions() {
  typeset IFS='.'
  typeset -a v1=($1)
  typeset -a v2=($2)
  typeset n diff

  for ((n = 0; n < $3; n += 1)); do
    diff=$((v1[n] - v2[n]))
    if [ $diff -ne 0 ]; then
      [ $diff -le 0 ] && echo '-1' || echo '1'
      return
    fi
  done
  echo '0'
}

#restart rsyslog
restartRsyslog() {
  logMsgToConfigSysLog "INFO" "INFO: Restarting the $RSYSLOG_SERVICE service."
  service $RSYSLOG_SERVICE restart
  if [ $? -ne 0 ]; then
    logMsgToConfigSysLog "WARNING" "WARNING: $RSYSLOG_SERVICE did not restart gracefully. Please restart $RSYSLOG_SERVICE manually."
  fi
}

#logs message to config syslog
logMsgToConfigSysLog() {
  #$1 variable will be SUCCESS or ERROR or INFO or WARNING
  #$2 variable will be the message
  cslStatus=$1
  cslMessage=$2
  echo "$cslMessage"
  currentTime=$(date)

  #for Linux system, we need to use -d switch to decode base64 whereas
  #for Mac system, we need to use -D switch to decode
  varUname=$(uname)
  if [[ $varUname == 'Linux' ]]; then
    enabler=$(echo -n MWVjNGU4ZTEtZmJiMi00N2U3LTkyOWItNzVhMWJmZjVmZmUw | base64 -d)
  elif [[ $varUname == 'Darwin' ]]; then
    enabler=$(echo MWVjNGU4ZTEtZmJiMi00N2U3LTkyOWItNzVhMWJmZjVmZmUw | base64 -D)
  fi

  if [ $? -ne 0 ]; then
    echo "ERROR: Base64 decode is not supported on your Operating System. Please update your system to support Base64."
    exit 1
  fi

  sendPayloadToConfigSysLog "$cslStatus" "$cslMessage" "$enabler"

  #if it is an error, then log message "Script Failed" to config syslog and exit the script
  if [[ $cslStatus == "ERROR" ]]; then
    sendPayloadToConfigSysLog "ERROR" "Script Failed" "$enabler"
    if [ "$varUname" != "Darwin" ]; then
      echo $MANUAL_CONFIG_INSTRUCTION
    fi
    exit 1
  fi

  #if it is a success, then log message "Script Succeeded" to config syslog and exit the script
  if [[ $cslStatus == "SUCCESS" ]]; then
    sendPayloadToConfigSysLog "SUCCESS" "Script Succeeded" "$enabler"
    exit 0
  fi
}

#payload construction to send log to config syslog
sendPayloadToConfigSysLog() {
  if [ "$APP_TAG" = "" ]; then
    var="{\"sub-domain\":\"$LOGGLY_ACCOUNT\", \"user-name\":\"$LOGGLY_USERNAME\", \"customer-token\":\"$LOGGLY_AUTH_TOKEN\", \"host-name\":\"$HOST_NAME\", \"script-name\":\"$SCRIPT_NAME\", \"script-version\":\"$SCRIPT_VERSION\", \"status\":\"$1\", \"time-stamp\":\"$currentTime\", \"linux-distribution\":\"$LINUX_DIST\", \"messages\":\"$2\",\"rsyslog-version\":\"$RSYSLOG_VERSION\",\"insecure-mode\":\"$INSECURE_MODE\",\"suppress-enabled\":\"$SUPPRESS_PROMPT\",\"force-secure-enabled\":\"$FORCE_SECURE\",\"loggly-removed\":\"$LOGGLY_REMOVE\"}"
  else
    var="{\"sub-domain\":\"$LOGGLY_ACCOUNT\", \"user-name\":\"$LOGGLY_USERNAME\", \"customer-token\":\"$LOGGLY_AUTH_TOKEN\", \"host-name\":\"$HOST_NAME\", \"script-name\":\"$SCRIPT_NAME\", \"script-version\":\"$SCRIPT_VERSION\", \"status\":\"$1\", \"time-stamp\":\"$currentTime\", \"linux-distribution\":\"$LINUX_DIST\", $APP_TAG, \"messages\":\"$2\",\"rsyslog-version\":\"$RSYSLOG_VERSION\",\"insecure-mode\":\"$INSECURE_MODE\",\"suppress-enabled\":\"$SUPPRESS_PROMPT\",\"force-secure-enabled\":\"$FORCE_SECURE\",\"loggly-removed\":\"$LOGGLY_REMOVE\"}"
  fi
  curl -s -H "content-type:application/json" -d "$var" $LOGS_01_URL/inputs/$3 >/dev/null 2>&1
}

#$1 return the count of records in loggly, $2 is the query param to search in loggly
searchAndFetch() {
  url=$2

  result=$(curl -s -u $LOGGLY_USERNAME:$LOGGLY_PASSWORD $url)

  if [ -z "$result" ]; then
    logMsgToConfigSysLog "ERROR" "ERROR: Please check your network/firewall settings & ensure Loggly subdomain, username and password is specified correctly."
    exit 1
  fi
  id=$(echo "$result" | grep -v "{" | grep id | awk '{print $2}')
  # strip last double quote from id
  id="${id%\"}"
  # strip first double quote from id
  id="${id#\"}"
  url="$LOGGLY_ACCOUNT_URL/apiv2/events?rsid=$id"

  # retrieve the data
  result=$(curl -s -u $LOGGLY_USERNAME:$LOGGLY_PASSWORD $url)
  count=$(echo "$result" | grep total_events | awk '{print $2}')
  count="${count%\,}"
  eval $1="'$count'"
  if [ "$count" -gt 0 ]; then
    timestamp=$(echo "$result" | grep timestamp)
  fi
}

#get password in the form of asterisk
getPassword() {
  unset LOGGLY_PASSWORD
  prompt="Please enter Loggly Password:"
  while IFS= read -p "$prompt" -r -s -n 1 char; do
    if [[ $char == $'\0' ]]; then
      break
    fi
    prompt='*'
    LOGGLY_PASSWORD+="$char"
  done
  echo
}

#function to switch system logging to insecure mode if user runs the modular script in insecure mode
switchSystemLoggingToInsecure() {
  if [ -f $LOGGLY_RSYSLOG_CONFFILE ]; then
    EXISTING_SYSLOG_PORT=$(grep -Eow 6514 $LOGGLY_RSYSLOG_CONFFILE)
    if [[ $EXISTING_SYSLOG_PORT == 6514 ]]; then
      if [ "$SUPPRESS_PROMPT" == "false" ]; then
        while true; do
          read -p "You are running the script using insecure mode, but your system logs are using secure mode. The script only supports a single mode for both, so would you like to switch your system logs to insecure mode? (yes/no)" yn
          case $yn in
          [Yy]*)
            logMsgToConfigSysLog "INFO" "INFO: Going to overwrite the conf file: $LOGGLY_RSYSLOG_CONFFILE with insecure configuration"
            LOGGLY_TLS_SENDING="false"
            LOGGLY_SYSLOG_PORT=514
            break
            ;;
          [Nn]*)
            logMsgToConfigSysLog "INFO" "INFO: Please re-run the script in secure mode if you want to setup secure logging"
            exit 1
            ;;
          *) echo "Please answer yes or no." ;;
          esac
        done
      else
        logMsgToConfigSysLog "WARN" "WARNING: You are running the script using insecure mode, but your system logs are using secure mode. The script only supports a single mode for both, so we are switching the system logs to insecure mode as well."
        LOGGLY_TLS_SENDING="false"
        LOGGLY_SYSLOG_PORT=514
      fi
    fi
  fi
}

#function to switch system logging to secure mode if user runs the modular script in secure mode
switchSystemLoggingToSecure() {
  if [ -f $LOGGLY_RSYSLOG_CONFFILE ]; then
    EXISTING_SYSLOG_PORT=$(grep -Eow 514 $LOGGLY_RSYSLOG_CONFFILE)
    if [[ $EXISTING_SYSLOG_PORT == 514 ]]; then
      if [ "$SUPPRESS_PROMPT" == "false" ]; then
        while true; do
          read -p "You are running the script using secure mode, but your system logs are using insecure mode. The script only supports a single mode for both, so would you like to switch your system logs to secure mode? (yes/no)" yn
          case $yn in
          [Yy]*)
            logMsgToConfigSysLog "INFO" "INFO: Going to overwrite the conf file: $LOGGLY_RSYSLOG_CONFFILE with secure configuration"
            LOGGLY_TLS_SENDING="true"
            LOGGLY_SYSLOG_PORT=6514
            break
            ;;
          [Nn]*)
            logMsgToConfigSysLog "INFO" "INFO: Please re-run the script in insecure mode if you want to setup insecure logging"
            exit 1
            ;;
          *) echo "Please answer yes or no." ;;
          esac
        done
      else
        logMsgToConfigSysLog "WARN" "WARNING: You are running the script using secure mode, but your system logs are using insecure mode. The script only supports a single mode for both, so we are switching the system logs to secure mode as well."
        LOGGLY_TLS_SENDING="true"
        LOGGLY_SYSLOG_PORT=6514
      fi
    fi
  fi
}

#check whether the user is running the script in secure or insecure mode and then switch system logging accordingly.
checkScriptRunningMode() {
  if [ "$FORCE_SECURE" == "false" ]; then
    if [[ $LOGGLY_SYSLOG_PORT == 514 ]]; then
      switchSystemLoggingToInsecure
    else
      switchSystemLoggingToSecure
    fi
  fi
}

#display usage syntax
usage() {
  cat <<EOF
usage: configure-linux [-a loggly auth account or subdomain] [-t loggly token (optional)] [-u username] [-p password (optional)] [-s suppress prompts {optional)] [--insecure {to send logs without TLS} (optional)[--force-secure {optional} ]
usage: configure-linux [-a loggly auth account or subdomain] [-r to remove]
usage: configure-linux [-h for help]
EOF
}

##########  Get Inputs from User - Start  ##########
if [ "$1" != "being-invoked" ]; then
  if [ $# -eq 0 ]; then
    usage
    exit
  else
    while [ "$1" != "" ]; do
      case $1 in
      -t | --token)
        shift
        LOGGLY_AUTH_TOKEN=$1
        echo "AUTH TOKEN $LOGGLY_AUTH_TOKEN"
        ;;
      -a | --account)
        shift
        LOGGLY_ACCOUNT=$1
        echo "Loggly account or subdomain: $LOGGLY_ACCOUNT"
        ;;
      -u | --username)
        shift
        LOGGLY_USERNAME=$1
        echo "Username is set"
        ;;
      -p | --password)
        shift
        LOGGLY_PASSWORD=$1
        ;;
      -r | --remove)
        LOGGLY_REMOVE="true"
        ;;
      -s | --suppress)
        SUPPRESS_PROMPT="true"
        ;;
      --insecure)
        LOGGLY_TLS_SENDING="false"
        LOGGLY_SYSLOG_PORT=514
        INSECURE_MODE="true"
        ;;
      --force-secure)
        FORCE_SECURE="true"
        LOGGLY_TLS_SENDING="true"
        LOGGLY_SYSLOG_PORT=6514
        ;;
      -h | --help)
        usage
        exit
        ;;
      *)
        usage
        exit
        ;;
      esac
      shift
    done
  fi

  if [ "$LOGGLY_REMOVE" == "true" -a "$LOGGLY_ACCOUNT" != "" ]; then
    removeLogglyConf
  elif [ "$LOGGLY_ACCOUNT" != "" -a "$LOGGLY_USERNAME" != "" ]; then
    if [ "$LOGGLY_PASSWORD" = "" ]; then
      getPassword
    fi
    installLogglyConf
  else
    usage
  fi
else
  IS_INVOKED="true"
fi

##########  Get Inputs from User - End  ##########       -------------------------------------------------------
#          End of Syslog Logging Directives for Loggly
#
