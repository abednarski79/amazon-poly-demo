# amazon-poly-demo

-- first sign up for AWS account
-- second in AWES create IAM user with access to polly service
mkdir $HOME/.aws
cd $HOME/.aws
vi credentials
-- enter data in format:
	[default]
	aws_access_key_id = xxx
	aws_secret_access_key = yyy
cd $HOME
git clone https://github.com/abednarski79/amazon-poly-demo.git
cd amazon-poly-demo
mvn clean install
cd target
java -jar java-demo-0.0.1-SNAPSHOT.jar
