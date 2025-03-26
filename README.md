# File Manager

file uploader


## 스펙
* Spring Boot Framework 2.7.16
* java 17
* Gradle
* yaml
* lombok
* Swagger API DOC

<br/>
<br/>


## 환경설정
* Intellij 기준으로 framework 구성
* STS IDE사용시 별도 환경설정 필요할수 있음


<br/>
<br/>

## Plugins
>필수 플러그인(Intellij 기준)

* .ignore
* GitToolBox
* MyBatisX
* Lombok

>properties 한글깨짐
* STS : simple properties Editor 1.0.5 (Plugins 설치필요).
* Intellij : Preferences > Editor > File Encodings, Transparent native-to-ascii conversion 를 체크, UTF-8 변경 후 Apply.

>Search .yml or properties
* Intellij : Spring Intilializr and Assistant (Plugins), 설치시 유용.

>머신러닝 AI, 효율적 개발 코드 검색.
* Intellij : Tabnine (Plugins)



<br/>
<br/>

## gradle 빌드
>Windows
>Linux


## 서버 실행 (profiles: prod, dev, local)
>기본 프로파일
* profiles: local
* port: 8080

>Windows
* 콘솔 실행: java -jar -Dspring.profiles.active=prod -Duser.timezone=Asia/Seoul -Dserver.port=8080 azure-blob-storage-file-api.jar
* 백그라운드 실행: javaw -jar -Dspring.profiles.active=prod -Duser.timezone=Asia/Seoul -Dserver.port=8080 azure-blob-storage-file-api.jar
>Linux
* 콘솔 실행: java -jar -Dspring.profiles.active=prod -Duser.timezone=Asia/Seoul -Dserver.port=8080 azure-blob-storage-file-api.jar
* 백그라운드 실행: nohup -jar -Dspring.profiles.active=prod -Duser.timezone=Asia/Seoul -Dserver.port=8080 azure-blob-storage-file-api.jar

## 서버 종료
>Windows
* 콘솔 실행: Ctrl + c
* 백그라운드 실행:
  * netstat -ano | findstr :포트번호
  * taskkill /f /pid 프로세스번호
>Linux
* 콘솔 실행: Ctrl + c
* 백그라운드 실행:
  * ps -ef | grep java | grep azure-blob-storage-file-api.jar |  grep -v 'grep' | grep -v 'nohup' | awk '{print $2}'
  * taskkill /f /pid 프로세스번호

## 실행스크립트 (run.sh)
* run.sh dev
* run.sh prod
* run.sh stop
```text
#!/bin/sh
SERVICE_NAME=azure-blob-storage-file-api
JAR_PATH=
JAR_NAME=azure-blob-storage-file-api.jar

#Get Current PID
CURRENT_PID=$(ps -ef | grep java | grep $JAR_NAME |  grep -v 'grep' | grep -v 'nohup' | awk '{print $2}')


case $1 in
    dev)
        echo "Starting $SERVICE_NAME ..."
        if [ -z $CURRENT_PID ]
		then
            echo "$SERVICE_NAME is already running ..."
        else
            nohup java -jar -Dspring.profiles.active=dev -Duser.timezone=Asia/Seoul -Dserver.port=8080 $JAR_PATH$JAR_NAME 1>/dev/null 2>&1 &
            echo "$SERVICE_NAME started ..."
        fi
    ;;
    prod)
        echo "Starting $SERVICE_NAME ..."
		if [ -z $CURRENT_PID ]
		then
            echo "$SERVICE_NAME is already running ..."
        else
            nohup java -jar -Dspring.profiles.active=prod -Duser.timezone=Asia/Seoul -Dserver.port=8080 $JAR_PATH$JAR_NAME 1>/dev/null 2>&1 &
            echo "$SERVICE_NAME started ..."
        fi
    ;;
    stop)
		# java kill
		if [ -z $CURRENT_PID ]
		then
		    echo "=====> $SERVICE_NAME is not running"
		else
		    echo "=====> $SERVICE_NAME kill"
		    sudo kill -15 $CURRENT_PID
		    sleep 5
		fi
    ;;
 
esac
```