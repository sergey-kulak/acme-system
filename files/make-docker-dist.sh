DIST="target/dist"
ARTIFACT=$1

rm -f -r $DIST
mkdir -p $DIST
(cd $DIST && jar -xf ../${ARTIFACT}*.jar)
cp Dockerfile $DIST