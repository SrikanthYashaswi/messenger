scp -i ~/.ssh/mob-prod.pem messenger-0.0.1-SNAPSHOT.jar newgen_admin@54.174.137.91:~/App/
~/run/p3.sh
sudo java -jar messenger-0.0.1-SNAPSHOT.jar 9876 &