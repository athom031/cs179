file_names=`ls phase2-tests/*.java`
for entry in $file_names; do
    echo $entry;
 	java Typecheck < $entry; 
    echo '---------------------------------------------------'
done
