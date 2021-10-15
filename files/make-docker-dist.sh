MODULE=$1
DIST="target/dist"
ARTIFACT=${2:-$MODULE}

rm -f -r $DIST
mkdir -p $DIST
cp target/${ARTIFACT}*.jar $DIST