#!/bin/sh

src="src"
dist="dist"
build="classes"

[ -d ${src}/ie/omk/smpp ] || {
	echo "Not in top level of SmppAPI distribution." 1>&2
	exit 1
}

if [ "$1" = "clean" ]; then
	rm -rf $dist
	rm -rf $build
	exit 0
fi


mkdir ${build}
javac -sourcepath ${src} -d ${build} ${src}/ie/omk/debug/*.java
javac -sourcepath ${src} -d ${build} ${src}/ie/omk/smpp/message/*.java
javac -sourcepath ${src} -d ${build} ${src}/ie/omk/smpp/net/*.java
javac -sourcepath ${src} -d ${build} ${src}/ie/omk/smpp/*.java

mkdir ${dist}
curd=`pwd`
cd ${build}
jar cf ${curd}/${dist}/smpp.jar ie
cd $curd
