DIST="target/dist"
ARTIFACT=$1

rm -f -r $DIST
mkdir -p $DIST
cp target/${ARTIFACT}*.jar $DIST
cp Dockerfile $DIST