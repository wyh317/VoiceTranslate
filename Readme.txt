mvn dependency:copy-dependencies
mvn package
java -cp "target/VoiceTranslate-1.0-SNAPSHOT.jar;target/dependency/*" org.example.VoiceTranslate zh-CN en-US zh-CN-XiaochenNeural
