#!/bin/sh

VERSION=2.0.8
KOTLIN_VERSION=2.1.10

while getopts v:h: flag
do
    case "${flag}" in
        v) VERSION=${OPTARG};;
    esac
done

sudo -v && sudo="true" || sudo=""

if ! [[ -x "$(command -v danger)" ]]; then
	if ! [[ -x "$(command -v npm)" ]]; then
		echo "Please install node js"
		exit 1
	fi

	echo "Installing danger"

	if [[ -n "$sudo" ]]; then
		sudo npm install -g danger
	else
		npm install -g danger
	fi
fi

if [[ -n "$sudo" && "$OSTYPE" != "darwin"* ]]; then
	sudo chmod -R a+rwx /usr/local/
fi

if ! [[ -x "$(command -v kotlinc)" ]]; then
    echo "Installing kotlin compiler ${KOTLIN_VERSION}"
    curl -o kotlin-compiler.zip -L "https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip"
    unzip -d /usr/local/ kotlin-compiler.zip
    echo 'export PATH=/usr/local/kotlinc/bin:$PATH' >> ~/.bash_profile
    rm -rf kotlin-compiler.zip
fi

if ! [[ -x "$(command -v gradle)" ]]; then
    echo "Installing gradle 8.10.2"
    curl -o gradle.zip -L https://downloads.gradle-dn.com/distributions/gradle-8.10.2-bin.zip
    mkdir /opt/gradle
    unzip -d /opt/gradle gradle.zip
    echo 'export PATH=/opt/gradle/gradle-8.10.2/bin:$PATH' >> ~/.bash_profile
    rm -rf gradle.zip
fi

git clone https://github.com/r0adkll/danger-kotlin.git --branch "$VERSION" --depth 1 _danger-kotlin
cd _danger-kotlin && make install
cd ..
rm -rf _danger-kotlin
