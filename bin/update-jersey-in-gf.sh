#!/bin/bash
 
JERSEY_VERSION=2.22-SNAPSHOT
HK2_VERSION=2.4.0-31
JAVASSIST_VERSION=3.18.1-GA
JACKSON_VERSION=2.5.4
 
MODULES_DIR=$GF_HOME/glassfish/modules
OSGI_CACHE_DIR=$GF_HOME/glassfish/domains/domain1/osgi-cache/felix
 
processArtifact() {
    # Backup artifact
    if [ ! -f ${MODULES_DIR}/$1.bak ]; then
        echo "Backuping $1 ..."
        cp ${MODULES_DIR}/$1 ${MODULES_DIR}/$1.bak
    fi
    
    # Download new version 
    echo "Downloading $1 ..."
    
    if curl -s -o ${MODULES_DIR}/$1 $2 ; then
        echo "Downloaded $1"
    else
        echo "Error downloading $1"
    fi
 
    return 0
}

processLocalArtifact() {
    # Backup artifact
    if [ ! -f ${MODULES_DIR}/$1.bak ]; then
        echo "Backuping $1 ..."
        cp ${MODULES_DIR}/$1 ${MODULES_DIR}/$1.bak
    fi

    # Copy new version
    echo "Copy $2 ..."

    if cp $2 ${MODULES_DIR}/$1 ; then
        echo "Copied $1"
    else
        echo "Error copying $1"
    fi

    return 0
}

# Backup and download new Jersey
 
# processLocalArtifact jersey-gf-cdi.jar $HOME/.m2/repository/org/glassfish/jersey/containers/glassfish/jersey-gf-cdi/${JERSEY_VERSION}/jersey-gf-cdi-${JERSEY_VERSION}.jar
processLocalArtifact jersey-gf-ejb.jar $HOME/.m2/repository/org/glassfish/jersey/containers/glassfish/jersey-gf-ejb/${JERSEY_VERSION}/jersey-gf-ejb-${JERSEY_VERSION}.jar
processLocalArtifact jersey-container-grizzly2-http.jar $HOME/.m2/repository/org/glassfish/jersey/containers/jersey-container-grizzly2-http/${JERSEY_VERSION}/jersey-container-grizzly2-http-${JERSEY_VERSION}.jar
processLocalArtifact jersey-container-servlet-core.jar $HOME/.m2/repository/org/glassfish/jersey/containers/jersey-container-servlet-core/${JERSEY_VERSION}/jersey-container-servlet-core-${JERSEY_VERSION}.jar
processLocalArtifact jersey-container-servlet.jar $HOME/.m2/repository/org/glassfish/jersey/containers/jersey-container-servlet/${JERSEY_VERSION}/jersey-container-servlet-${JERSEY_VERSION}.jar
processLocalArtifact jersey-client.jar $HOME/.m2/repository/org/glassfish/jersey/core/jersey-client/${JERSEY_VERSION}/jersey-client-${JERSEY_VERSION}.jar
processLocalArtifact jersey-common.jar $HOME/.m2/repository/org/glassfish/jersey/core/jersey-common/${JERSEY_VERSION}/jersey-common-${JERSEY_VERSION}.jar
processLocalArtifact jersey-server.jar $HOME/.m2/repository/org/glassfish/jersey/core/jersey-server/${JERSEY_VERSION}/jersey-server-${JERSEY_VERSION}.jar
processLocalArtifact jersey-bean-validation.jar $HOME/.m2/repository/org/glassfish/jersey/ext/jersey-bean-validation/${JERSEY_VERSION}/jersey-bean-validation-${JERSEY_VERSION}.jar
processLocalArtifact jersey-entity-filtering.jar $HOME/.m2/repository/org/glassfish/jersey/ext/jersey-entity-filtering/${JERSEY_VERSION}/jersey-entity-filtering-${JERSEY_VERSION}.jar
processLocalArtifact jersey-mvc-jsp.jar $HOME/.m2/repository/org/glassfish/jersey/ext/jersey-mvc-jsp/${JERSEY_VERSION}/jersey-mvc-jsp-${JERSEY_VERSION}.jar
processLocalArtifact jersey-mvc.jar $HOME/.m2/repository/org/glassfish/jersey/ext/jersey-mvc/${JERSEY_VERSION}/jersey-mvc-${JERSEY_VERSION}.jar
processLocalArtifact jersey-media-json-jackson.jar $HOME/.m2/repository/org/glassfish/jersey/media/jersey-media-json-jackson/${JERSEY_VERSION}/jersey-media-json-jackson-${JERSEY_VERSION}.jar
processLocalArtifact jersey-media-json-jettison.jar $HOME/.m2/repository/org/glassfish/jersey/media/jersey-media-json-jettison/${JERSEY_VERSION}/jersey-media-json-jettison-${JERSEY_VERSION}.jar
processLocalArtifact jersey-media-json-processing.jar $HOME/.m2/repository/org/glassfish/jersey/media/jersey-media-json-processing/${JERSEY_VERSION}/jersey-media-json-processing-${JERSEY_VERSION}.jar
processLocalArtifact jersey-media-moxy.jar $HOME/.m2/repository/org/glassfish/jersey/media/jersey-media-moxy/${JERSEY_VERSION}/jersey-media-moxy-${JERSEY_VERSION}.jar
processLocalArtifact jersey-media-multipart.jar $HOME/.m2/repository/org/glassfish/jersey/media/jersey-media-multipart/${JERSEY_VERSION}/jersey-media-multipart-${JERSEY_VERSION}.jar
processLocalArtifact jersey-media-sse.jar $HOME/.m2/repository/org/glassfish/jersey/media/jersey-media-sse/${JERSEY_VERSION}/jersey-media-sse-${JERSEY_VERSION}.jar
processLocalArtifact jersey-guava.jar $HOME/.m2/repository/org/glassfish/jersey/bundles/repackaged/jersey-guava/${JERSEY_VERSION}/jersey-guava-${JERSEY_VERSION}.jar
processLocalArtifact jersey-cdi1x-transaction.jar $HOME/.m2/repository/org/glassfish/jersey/ext/cdi/jersey-cdi1x-transaction/${JERSEY_VERSION}/jersey-cdi1x-transaction-${JERSEY_VERSION}.jar
processLocalArtifact jersey-cdi1x-servlet.jar $HOME/.m2/repository/org/glassfish/jersey/ext/cdi/jersey-cdi1x-servlet/${JERSEY_VERSION}/jersey-cdi1x-servlet-${JERSEY_VERSION}.jar
processLocalArtifact jersey-cdi1x.jar $HOME/.m2/repository/org/glassfish/jersey/ext/cdi/jersey-cdi1x/${JERSEY_VERSION}/jersey-cdi1x-${JERSEY_VERSION}.jar
processLocalArtifact jersey-media-jaxb.jar $HOME/.m2/repository/org/glassfish/jersey/media/jersey-media-jaxb/${JERSEY_VERSION}/jersey-media-jaxb-${JERSEY_VERSION}.jar

# Backup and download new HK2
 
#processArtifact hk2-api.jar http://central.maven.org/maven2/org/glassfish/hk2/hk2-api/${HK2_VERSION}/hk2-api-${HK2_VERSION}.jar
#processArtifact class-model.jar http://central.maven.org/maven2/org/glassfish/hk2/class-model/${HK2_VERSION}/class-model-${HK2_VERSION}.jar
#processArtifact core.jar http://central.maven.org/maven2/org/glassfish/hk2/core/${HK2_VERSION}/core-${HK2_VERSION}.jar
#processArtifact hk2-locator.jar http://central.maven.org/maven2/org/glassfish/hk2/hk2-locator/${HK2_VERSION}/hk2-locator-${HK2_VERSION}.jar
#processArtifact hk2-utils.jar http://central.maven.org/maven2/org/glassfish/hk2/hk2-utils/${HK2_VERSION}/hk2-utils-${HK2_VERSION}.jar
#processArtifact hk2.jar http://central.maven.org/maven2/org/glassfish/hk2/hk2/${HK2_VERSION}/hk2-${HK2_VERSION}.jar
#processArtifact hk2-runlevel.jar http://central.maven.org/maven2/org/glassfish/hk2/hk2-runlevel/${HK2_VERSION}/hk2-runlevel-${HK2_VERSION}.jar
#processArtifact hk2-config.jar http://central.maven.org/maven2/org/glassfish/hk2/hk2-config/${HK2_VERSION}/hk2-config-${HK2_VERSION}.jar
#processArtifact osgi-adapter.jar http://central.maven.org/maven2/org/glassfish/hk2/osgi-adapter/${HK2_VERSION}/osgi-adapter-${HK2_VERSION}.jar

#processArtifact bean-validator-cdi.jar http://central.maven.org/maven2/org/glassfish/hk2/external/bean-validator-cdi/${HK2_VERSION}/bean-validator-cdi-${HK2_VERSION}.jar
#processArtifact bean-validator.jar http://central.maven.org/maven2/org/glassfish/hk2/external/bean-validator/${HK2_VERSION}/bean-validator-${HK2_VERSION}.jar
#processArtifact aopalliance-repackaged.jar http://central.maven.org/maven2/org/glassfish/hk2/external/aopalliance-repackaged/${HK2_VERSION}/aopalliance-repackaged-${HK2_VERSION}.jar
#processArtifact asm-all-repackaged.jar http://central.maven.org/maven2/org/glassfish/hk2/external/asm-all-repackaged/${HK2_VERSION}/asm-all-repackaged-${HK2_VERSION}.jar
#processArtifact javax.inject.jar http://central.maven.org/maven2/org/glassfish/hk2/external/javax.inject/${HK2_VERSION}/javax.inject-${HK2_VERSION}.jar
 
processArtifact javassist.jar http://repo.maven.apache.org/maven2/org/javassist/javassist/${JAVASSIST_VERSION}/javassist-${JAVASSIST_VERSION}.jar

processArtifact jackson-core.jar http://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-core/${JACKSON_VERSION}/jackson-core-${JACKSON_VERSION}.jar
processArtifact jackson-databind.jar http://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-databind/${JACKSON_VERSION}/jackson-databind-${JACKSON_VERSION}.jar
processArtifact jackson-annotations.jar http://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-annotations/${JACKSON_VERSION}/jackson-annotations-${JACKSON_VERSION}.jar
processArtifact jackson-jaxrs-base.jar http://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-annotations/${JACKSON_VERSION}/jackson-annotations-${JACKSON_VERSION}.jar
processArtifact jackson-annotations.jar http://repo.maven.apache.org/maven2/com/fasterxml/jackson/jaxrs/jackson-jaxrs-base/${JACKSON_VERSION}/jackson-jaxrs-base-${JACKSON_VERSION}.jar
processArtifact jackson-jaxrs-json-provider.jar http://repo.maven.apache.org/maven2/com/fasterxml/jackson/jaxrs/jackson-jaxrs-json-provider/${JACKSON_VERSION}/jackson-jaxrs-json-provider-${JACKSON_VERSION}.jar

# Clean up OSGi cache
 
if [ -d "$OSGI_CACHE_DIR" ]; then
    rm -rf $OSGI_CACHE_DIR
fi
