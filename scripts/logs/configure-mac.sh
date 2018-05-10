#!/bin/bash

#trapping Control + C
#these statements must be the first statements in the script to trap the CTRL C event

trap ctrl_c INT

function ctrl_c()  {
	logMsgToConfigSysLog "INFO" "INFO: Aborting the script."
	exit 1
}

##########  Variable Declarations - Start  ##########

#name of the current script. This will get overwritten by the child script which calls this
SCRIPT_NAME=configure-mac.sh
#version of the current script. This will get overwritten by the child script which calls this
SCRIPT_VERSION=1.4

#application tag. This will get overwritten by the child script which calls this
APP_TAG=

#this variable will hold the name of mac distribution
MAC_DIST=

FLUENTD_CONF=loggly-fluentd.conf

#ruby
RUBY=ruby

#minimum version of ruby
MIN_RUBY_VERSION=1.9.3

#this variable will hold the users ruby version
RUBY_VERSION=

#this variable will hold the host name
HOST_NAME=

#host name for logs-01.loggly.com
LOGS_01_HOST=logs-01.loggly.com
LOGS_01_URL=https://$LOGS_01_HOST

#this variable will contain loggly account url in the format https://$LOGGLY_ACCOUNT.loggly.com
LOGGLY_ACCOUNT_URL=

#loggly.com URL
LOGGLY_COM_URL=https://www.loggly.com

#installation directory
LOGGLY_HOME=$HOME/.loggly

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

#variables used in fluentd.conf file
LOGGLY_SYSLOG_PORT=514
LOGGLY_DISTRIBUTION_ID="41058"

#this variable is set if the script is invoked via some other calling script
IS_INVOKED=

#this variable will hold if the check env function for linux is invoked
MAC_ENV_VALIDATED="false"

#this variable will inform if verification needs to be performed
MAC_DO_VERIFICATION="true"

#if this variable is set to true then suppress all prompts
SUPPRESS_PROMPT="false"

#plist file path
PROP_FILE=

#manual instructions to be show in case of error
MANUAL_CONFIG_INSTRUCTION="Manual instructions to configure Loggly on Mac are available at https://www.loggly.com/docs/send-mac-logs-to-loggly/."

MANUAL_XCODE_INSTALL_INSTRUCTION="Xcode command line tools are not installed on your system. Try running \"xcode-select --install\" to install xcode command line tools and run script again. You can download tools manually from https://developer.apple.com/"

checkMacLogglyCompatibility()
{	
	#check if the user has root permission to run this script
	checkIfUserHasRootPrivileges

	#check if the OS is supported by the script. If no, then exit
	checkIfSupportedOS

	#set the basic variables needed by this script
	setMacVariables

	#check if the Loggly servers are accessible. If no, ask user to check network connectivity & exit
	checkIfLogglyServersAccessible

	#check if user credentials are valid. If no, then exit
	checkIfValidUserNamePassword

	#get authentication token if not provided
	getAuthToken

	#check if authentication token is valid. If no, then exit.
	checkIfValidAuthToken
	
	#check if minimum version of ruby is installed
	checkIfMinRubyVersionInstalled
	
     	#check if xcode command line tools are installed
    	checkIfXCodeCommandlineToolsInstalled

    	MAC_ENV_VALIDATED="true"
}

# executing the script for loggly to install and configure fluentd.
installLogglyConf()
{
	#log message indicating starting of Loggly configuration
	logMsgToConfigSysLog "INFO" "INFO: Initiating Configure Loggly for Mac."

	if [ "$MAC_ENV_VALIDATED" = "false" ]; then
		checkMacLogglyCompatibility
	fi

	#checks if fluentd already installed otherwise installs it
	checkIfFluentdInstalled
	
	#installs Loggly fluentd plugin
	installLogglyFluentdPlugin
	
	#write conf file in the system
	writeLogglyConfFile
	
	#configures Fluentd service
	configureFluentdAsService
	
	#starts Fluentd service
	startFluentdService

	if [ "$MAC_DO_VERIFICATION" = "true" ]; then
		#check if the logs are going to loggly fro mac system now
		checkIfLogsMadeToLoggly
	fi

	if [ "$IS_INVOKED" = "" ]; then
		logMsgToConfigSysLog "SUCCESS" "SUCCESS: Mac system successfully configured to send logs via Loggly."
	fi
}	


#remove loggly configuration from Linux system
removeLogglyConf()
{
	#log message indicating starting of Loggly configuration
	logMsgToConfigSysLog "INFO" "INFO: Initiating uninstall Loggly for Mac."

	#check if the user has root permission to run this script
	checkIfUserHasRootPrivileges

	#check if the OS is supported by the script. If no, then exit
	checkIfSupportedOS

	#set the basic variables needed by this script
	setMacVariables

	#remove loggly.conf file
	removeLogglyConfFile


	#log success message
	logMsgToConfigSysLog "SUCCESS" "SUCCESS: Uninstalled Loggly configuration from Mac system."
}

#checks if the script is run as a sudo user
checkIfUserHasRootPrivileges()
{
	#This script needs to be run as a sudo user
	if [[ $EUID -ne 0 ]]; then
	   logMsgToConfigSysLog "ERROR" "ERROR: This script must be run as root."
	   exit 1
	fi
}


#check if supported operating system
checkIfSupportedOS()
{
    	# Determine OS platform
    	UNAME=$(uname | tr "[:upper:]" "[:lower:]")
    	MAC_DIST=$UNAME
	if [ "$MAC_DIST" == "darwin" ]; then
		logMsgToConfigSysLog "INFO" "INFO: Operating system is Mac"
	else
		logMsgToConfigSysLog "ERROR": "ERROR: This script supports only Mac systems. You can find alternative options here: https://www.loggly.com/docs/sending-logs-unixlinux-system-setup/"
		exit 1
	fi
}


#sets mac variables which will be used across various functions
setMacVariables()
{
	#set host name
	HOST_NAME=$(hostname)

	#set loggly account url
	LOGGLY_ACCOUNT_URL=https://$LOGGLY_ACCOUNT.loggly.com
}


#checks if all the various endpoints used for configuring loggly are accessible
checkIfLogglyServersAccessible()
{
	logMsgToConfigSysLog "INFO" "INFO: Checking if $LOGS_01_HOST is reachable."
        if [ $(ping -c 1 $LOGS_01_HOST | grep "1 packets transmitted, 1 packets received, 0.0% packet loss" | wc -l) == 1 ]; then
                logMsgToConfigSysLog "INFO" "INFO: $LOGS_01_HOST is reachable."
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
	if [ $(curl -s --head  --request GET $LOGGLY_ACCOUNT_URL/login | grep "200 OK" | wc -l) == 1 ]; then
		echo "INFO: $LOGGLY_ACCOUNT_URL is valid and reachable."
	else
		logMsgToConfigSysLog "ERROR" "ERROR: This is not a recognized subdomain. Please ask the account owner for the subdomain they signed up with."
		exit 1
	fi
}

#check if user name and password is valid
checkIfValidUserNamePassword()
{
	echo "INFO: Checking if provided username and password is correct."
	if [ $(curl -s -u $LOGGLY_USERNAME:$LOGGLY_PASSWORD $LOGGLY_ACCOUNT_URL/apiv2/customer | grep "Unauthorized" | wc -l) == 1 ]; then
		logMsgToConfigSysLog "INFO" "INFO: Please check your username or reset your password at $LOGGLY_ACCOUNT_URL/account/users/"
		logMsgToConfigSysLog "ERROR" "ERROR: Invalid Loggly username or password. Your username is visible at the top right of the Loggly console before the @ symbol. You can reset your password at http://<subdomain>.loggly.com/login."
		exit 1
	else
		logMsgToConfigSysLog "INFO" "INFO: Username and password authorized successfully."
	fi
}

#gets the authentication token from the Loggly server
getAuthToken()
{
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
checkIfValidAuthToken()
{
	echo "INFO: Checking if provided auth token is correct."
	if [ $(curl -s -u $LOGGLY_USERNAME:$LOGGLY_PASSWORD $LOGGLY_ACCOUNT_URL/apiv2/customer | grep \"$LOGGLY_AUTH_TOKEN\" | wc -l) == 1 ]; then
		logMsgToConfigSysLog "INFO" "INFO: Authentication token validated successfully."
	else
		logMsgToConfigSysLog "ERROR" "ERROR: Invalid authentication token $LOGGLY_AUTH_TOKEN. You can get valid authentication token by following instructions at https://www.loggly.com/docs/customer-token-authentication-token/."
		exit 1
	fi
}

#this functions check if the min required version is installed in  the system
checkIfMinRubyVersionInstalled()
{
    RUBY_VERSION=$(sudo $RUBY --version | grep "$RUBY")
    RUBY_VERSION=${RUBY_VERSION%p*}
    RUBY_VERSION=${RUBY_VERSION#* }
    RUBY_VERSION=$RUBY_VERSION | tr -d " "
    if [ $(compareVersions $RUBY_VERSION $MIN_RUBY_VERSION 3) -lt 0 ]; then
        logMsgToConfigSysLog "ERROR" "ERROR: Min ruby version required is 1.9.3."
        exit 1
    fi
}

checkIfXCodeCommandlineToolsInstalled()
{
    logMsgToConfigSysLog "INFO" "INFO: Checking if Xcode command line tools are installed."
    
    if [ $(xcode-select -p 2>/dev/null | wc -l ) == 0 ]; then
        logMsgToConfigSysLog "ERROR" "ERROR: $MANUAL_XCODE_INSTALL_INSTRUCTION"
        exit 1
    else
        logMsgToConfigSysLog "INFO" "INFO: Xcode command line tools are installed in your system."
    fi
}

#this functions checks if the Fluentd gem is installed in the system
checkIfFluentdInstalled()
{
    if [ $(sudo fluentd --setup $LOGGLY_HOME/fluent 2>/dev/null  | grep ".loggly/fluent/fluent.conf" | wc -l ) == 1 ]; then
        logMsgToConfigSysLog "INFO" "INFO: Fluentd is already installed. Not installing."
    else
    	logMsgToConfigSysLog "INFO" "INFO: Fluentd is not installed. Installing Fluentd. This may take a while."
        installFluentd
    fi
}

#this function installs the Fluentd in the system
installFluentd()
{
	#install fluentd gem http://docs.fluentd.org/articles/install-by-gem
	sudo gem install fluentd --no-ri --no-rdoc -n/usr/local/bin
	
	if [[ ! -d "$LOGGLY_HOME" ]]; then
                mkdir $LOGGLY_HOME
        fi
	
	#to check fluentd installed successfully
	if [ $(sudo fluentd --setup $LOGGLY_HOME/fluent 2>/dev/null  | grep ".loggly/fluent/fluent.conf" | wc -l ) == 1 ]; then
		logMsgToConfigSysLog "INFO" "INFO: Fluentd installed Successfully"
	else
		logMsgToConfigSysLog "ERROR" "ERROR: Unable to install fluentd"
		exit 1
	fi
}

#this function installs Loggly fluentd plugin
installLogglyFluentdPlugin()
{
    	logMsgToConfigSysLog "INFO" "INFO: Installing Loggly plugin for Fluentd"
    	sudo gem install fluent-plugin-loggly
	logMsgToConfigSysLog "INFO" "INFO: Loggly fluentd plugin installed successfully."
}

#function to write the contents of fluentd config file
writeLogglyConfFile()
{	

	FLUENTD_CONF="$HOME/.loggly/fluentd-loggly.conf"

	if [ -f "$FLUENTD_CONF" ]; then
		echo "INFO: Conf file already exists. Creating Backup $FLUENTD_CONF $FLUENTD_CONF.bk"
		sudo mv $FLUENTD_CONF $FLUENTD_CONF.bk
	fi
	
	logMsgToConfigSysLog "INFO" "INFO: Creating file $FLUENTD_CONF"

	sudo touch $FLUENTD_CONF
	
inputStr="
<source>
   type tail
   format none
   path /var/log/system.log
   tag system_logs
</source>
<match **>
   type loggly
   loggly_url  http://logs-01.loggly.com/inputs/$LOGGLY_AUTH_TOKEN/tag/Mac
</match>"

sudo cat << EOIPFW >> $FLUENTD_CONF
$inputStr
EOIPFW

}

#delete 22-loggly.conf file
removeLogglyConfFile()
{
	if [ -f "$HOME/.loggly/fluentd-loggly.conf" ]; then
		logMsgToConfigSysLog "INFO" "INFO: Deleting file fluentd-loggly.conf"
		sudo rm -rf "$HOME/.loggly/fluentd-loggly.conf"
		
		logMsgToConfigSysLog "INFO" "INFO: Removing Fluentd service"
		sudo launchctl unload -F /Library/LaunchDaemons/com.loggly.loggly_fluentd.plist > /dev/null 2>&1
		sudo rm -rf /Library/LaunchDaemons/com.loggly.loggly_fluentd.plist
	else
		logMsgToConfigSysLog "ERROR" "ERROR: There is no conf file to delete"	
		exit 1
	fi
}

#this function creates a fluentd daemon to send logs to Loggly
configureFluentdAsService()
{
	logMsgToConfigSysLog "INFO" "INFO: Creating daemon for Loggly conf file."
	
	#this sets the fluentd installation location
	FLUENTD_LOCATION=$(which fluentd)
	
	PROP_FILE="/Library/LaunchDaemons/com.loggly.loggly_fluentd.plist"
	
	#if loggly fluentd is already running as a service then unload it
	if [ $(sudo launchctl list | grep 'com.loggly.loggly_fluentd' | wc -l) == 1 ]; then
	    sudo launchctl unload -F $PROP_FILE > /dev/null 2>&1
		
		#if there was some error while unloading, just remove it
		sudo launchctl remove com.loggly.loggly_fluentd  > /dev/null 2>&1
	fi
	
	#if plist file is already there then delete it
	if [ -f "$PROP_FILE" ]; then
	    sudo rm -f $PROP_FILE
	fi
	
	sudo touch $PROP_FILE
    	sudo chmod +x $PROP_FILE

propStr="
<?xml version="1.0" encoding="UTF-8"?>
<plist version="1.0">
	<dict>
		<key>Label</key>
		<string>com.loggly.loggly_fluentd</string>
		<key>ProgramArguments</key>
		<array>
			<string>$FLUENTD_LOCATION</string>
			<string>-c</string>
			<string>$HOME/.loggly/fluentd-loggly.conf</string>
		</array>
		<key>RunAtLoad</key>
		<true/>
		<key>StandardErrorPath</key>
		<string>/tmp/loggly_fluentd.err</string>
		<key>StandardOutPath</key>
		<string>/tmp/loggly_fluentd.out</string>
	</dict>
</plist>"

sudo cat << EOIPFW >> $PROP_FILE
$propStr
EOIPFW

}

#starts Fluentd Service
startFluentdService()
{
	logMsgToConfigSysLog "INFO" "INFO: Starting Fluentd as a service"
	sudo launchctl load -F $PROP_FILE
	logMsgToConfigSysLog "INFO" "INFO: Fluentd started successfully"
}

#check if the logs made it to Loggly
checkIfLogsMadeToLoggly()
{
    	logMsgToConfigSysLog "INFO" "INFO: Sending test message to Loggly. Waiting for 30 secs."
    
    	#sleeping for 30 secs so that fluentd service can start doing its work properly
    	sleep 30
	uuid=$(cat /dev/urandom | env LC_CTYPE=C tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)	

	queryParam="tag%3AMac%20$uuid"
	logger -t "Mac" "Mac-Test message for verification with UUID $uuid"

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
			logMsgToConfigSysLog "SUCCESS" "SUCCESS: Verification logs successfully transferred to Loggly! You are now sending Mac system logs to Loggly."
	 		exit 0
		else
			logMsgToConfigSysLog "INFO" "SUCCESS: Verification logs successfully transferred to Loggly! You are now sending Mac system logs to Loggly."
 		fi
	fi

}

compareVersions ()
{
	typeset    IFS='.'
	typeset -a v1=( $1 )
	typeset -a v2=( $2 )
	typeset    n diff

	for (( n=0; n<$3; n+=1 )); do
	diff=$((v1[n]-v2[n]))
	if [ $diff -ne 0 ] ; then
		[ $diff -le 0 ] && echo '-1' || echo '1'
		return
	fi
	done
	echo  '0'
}


#logs message to config syslog
logMsgToConfigSysLog()
{
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
		enabler=$(echo -n MWVjNGU4ZTEtZmJiMi00N2U3LTkyOWItNzVhMWJmZjVmZmUw | base64 --decode)
	elif [[ $varUname == 'Darwin' ]]; then
		enabler=$(echo MWVjNGU4ZTEtZmJiMi00N2U3LTkyOWItNzVhMWJmZjVmZmUw | base64 --decode)
	fi
	
	if [ $? -ne 0 ]; then
		echo  "ERROR: Base64 decode is not supported on your Operating System. Please update your system to support Base64."
		exit 1
	fi

	sendPayloadToConfigSysLog "$cslStatus" "$cslMessage" "$enabler"

	#if it is an error, then log message "Script Failed" to config syslog and exit the script
	if [[ $cslStatus == "ERROR" ]]; then
		sendPayloadToConfigSysLog "ERROR" "Script Failed" "$enabler"
		echo $MANUAL_CONFIG_INSTRUCTION
		exit 1
	fi

	#if it is a success, then log message "Script Succeeded" to config syslog and exit the script
	if [[ $cslStatus == "SUCCESS" ]]; then
		sendPayloadToConfigSysLog "SUCCESS" "Script Succeeded" "$enabler"
		exit 0
	fi
}


#payload construction to send log to config syslog
sendPayloadToConfigSysLog()
{
	if [ "$APP_TAG" = "" ]; then
		var="{\"sub-domain\":\"$LOGGLY_ACCOUNT\", \"user-name\":\"$LOGGLY_USERNAME\", \"customer-token\":\"$LOGGLY_AUTH_TOKEN\", \"host-name\":\"$HOST_NAME\", \"script-name\":\"$SCRIPT_NAME\", \"script-version\":\"$SCRIPT_VERSION\", \"status\":\"$1\", \"time-stamp\":\"$currentTime\", \"Mac-distribution\":\"$MAC_DIST\", \"messages\":\"$2\",\"ruby-version\":\"$RUBY_VERSION\"}"
	else
		var="{\"sub-domain\":\"$LOGGLY_ACCOUNT\", \"user-name\":\"$LOGGLY_USERNAME\", \"customer-token\":\"$LOGGLY_AUTH_TOKEN\", \"host-name\":\"$HOST_NAME\", \"script-name\":\"$SCRIPT_NAME\", \"script-version\":\"$SCRIPT_VERSION\", \"status\":\"$1\", \"time-stamp\":\"$currentTime\", \"Mac-distribution\":\"$MAC_DIST\", $APP_TAG, \"messages\":\"$2\",\"ruby-version\":\"$RUBY_VERSION\"}"
	fi
	curl -s -H "content-type:application/json" -d "$var" $LOGS_01_URL/inputs/$3 > /dev/null 2>&1
}

#$1 return the count of records in loggly, $2 is the query param to search in loggly
searchAndFetch()
{
	url=$2
	
	result=$(curl -s -u "$LOGGLY_USERNAME":"$LOGGLY_PASSWORD" "$url" )
	
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
	result=$(curl -s -u "$LOGGLY_USERNAME":"$LOGGLY_PASSWORD" "$url" )
	count=$(echo "$result" | grep total_events | awk '{print $2}')
	count="${count%\,}"
	eval $1="'$count'"
	if [ "$count" -gt 0 ]; then
		timestamp=$(echo "$result" | grep timestamp)
	fi	
}

#get password in the form of asterisk
getPassword()
{
	unset LOGGLY_PASSWORD
	prompt="Please enter Loggly Password:"
	while IFS= read -p "$prompt" -r -s -n 1 char
	do
		if [[ $char == $'\0' ]]
		then
			break
		fi
		prompt='*'
		LOGGLY_PASSWORD+="$char"
	done
	echo
}

#display usage syntax
usage()
{
cat << EOF
usage: configure-mac [-a loggly auth account or subdomain] [-t loggly token (optional)] [-u username] [-p password (optional)]
usage: configure-mac [-a loggly auth account or subdomain] [-r to remove]
usage: configure-mac [-h for help]
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
			-t | --token ) shift
				LOGGLY_AUTH_TOKEN=$1
				echo "AUTH TOKEN $LOGGLY_AUTH_TOKEN"
				;;
			-a | --account ) shift
				LOGGLY_ACCOUNT=$1
				echo "Loggly account or subdomain: $LOGGLY_ACCOUNT"
				;;
			-u | --username ) shift
				LOGGLY_USERNAME=$1
				echo "Username is set"
				;;
			-p | --password ) shift
				LOGGLY_PASSWORD=$1
				;;
			-r | --remove )
				LOGGLY_REMOVE="true"
				;;
			-h | --help)
				usage
				exit
				;;
			*) usage
			exit
			;;
			esac
			shift
		done
	fi

	if [ "$LOGGLY_REMOVE" != "" -a "$LOGGLY_ACCOUNT" != "" ]; then
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

##########  Get Inputs from User - End  ##########
