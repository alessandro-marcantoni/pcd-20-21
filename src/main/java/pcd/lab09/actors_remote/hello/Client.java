package pcd.lab09.actors_remote.hello;

import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.*;

public class Client {
  public static void main(String[] args) throws Exception {
	  Config config = ConfigFactory.parseFile(new File("src/main/java/pcd/lab09/actors_remote/hello/app2.conf"));
	  ActorSystem system = ActorSystem.create("MySystem",config);
	    
	  ActorSelection selection = system.actorSelection("akka://MySystem@127.0.0.1:25520/user/myActor");
	  selection.tell(new HelloMsg("World2"),ActorRef.noSender());
  }
}
