file_names=`ls phase2-tests/*.java`
for entry in $file_names; do
  echo $entry;
 	java J2V < $entry > out/$entry;
  java -jar vapor.jar run out/$entry;
  rm out/$entry
  echo '---------------------------------------------------'
done
