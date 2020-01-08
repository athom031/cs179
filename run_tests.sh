file_names=`ls phase1-tests/*.java`
for entry in $file_names; do
    echo $entry;
 	java Typecheck < $entry; 
done
