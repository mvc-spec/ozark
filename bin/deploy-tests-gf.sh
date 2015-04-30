
BASEDIR=$(dirname $0)

WARS=`find . -name '*.war' -print`

for i in $WARS
do
    asadmin --passwordfile $BASEDIR/gf-password.txt deploy --force $i
done
