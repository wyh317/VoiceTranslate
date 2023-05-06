mvn dependency:copy-dependencies
mvn package
java -cp "target/VoiceTranslate-1.0-SNAPSHOT.jar;target/dependency/*" org.example.VoiceTranslate en-US zh-CN zh-CN-XiaochenNeural
