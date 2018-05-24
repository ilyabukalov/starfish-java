/**
 *  
 * To handle API Calls from User Side 
 * This should take actorId... as parameters
 * This data should be returned 
 * For registering an user.
 * url to register an user   :  http://host:8000/api/v1/keeper/users/user/
 * paramter :actorId
 * Author : Aleena , Athul ,Arun (Uvionics Tec)
 */

package com.oceanprotocolclient.user;

import java.util.Arrays;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.oceanprotocolclient.interfaces.UserInterface;
import com.oceanprotocolclient.model.User;

public class UserController implements UserInterface {

	/**
	 * 
	 * @param actorId
	 * @return JSONObject
	 * 
	 * Registers an actor with the Ocean network. POST
	 * "/api/v1/keeper/actors/actor/"
	 */

	public User userRegistration(String actorId, String targetUrl) {
		// Create object for user class..it include all user details
		User user = new User();
		// Initialize postResp - response from ocean network is given to this
		// variable
		String postResp = null;
		try {
			// Used for generating a random Alphabet and add to ActorId
			Random rnd = new Random();
			char c = (char) (rnd.nextInt(26) + 'a');
			String s = Character.toString(c);
			actorId = s + actorId;

			/**
			 * Used for posting the data to ocean network
			 */
			PostMethod post = new PostMethod(targetUrl);
			post.setParameter("actorId", actorId);// set Parameter actorId
			HttpClient httpclient = new HttpClient();
			httpclient.executeMethod(post);
			// Response from ocean network
			postResp = post.getResponseBodyAsString();

			/**
			 * Used for return a Json Object with failed result and status
			 */
			if (postResp == null) {
				return user;
			}
			/**
			 * Used for getting WalletId and PrivateKey
			 */
			String prepostToJson = postResp.substring(1, postResp.length() - 1);
			// Data coming from ocean network is a json string..This line remove
			// the "\\" from the response
			String postToJson = prepostToJson.replaceAll("\\\\", "");
			JSONParser parser = new JSONParser(); // create json parser
			// parse the data to json object
			JSONObject json = (JSONObject) parser.parse(postToJson);
			// get the wallet address from ocean network response
			String walletId = (String) json.get("defaultWalletAddress");
			// set the wallet id to the user object
			user.setWalletId(walletId);
			// get the private key from ocean network response
			String privateKey = (String) json.get("privateKey");
			// set the private key to the user object
			user.setPrivateKey(privateKey);
			// set the actorid to the user object
			user.setActorId(actorId);
			// set the updateDatetime to the user object
			user.setUpdateDatetime(json.get("updateDatetime").toString());
			// set the user state to the user object
			user.setState((String) json.get("state"));
			// set the creationDatetime to the user object
			user.setCreationDatetime(json.get("creationDatetime").toString());
			/**
			 * Used for adding WalletId, PrivateKey,actorId and status to Json
			 * Object
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * 
	 * @param actorId
	 * @return JSONObject
	 * 
	 * This method used to get the actor details from ocean network GET
	 * "/api/v1/keeper/actors/actor/<actor_id>"
	 */

	public User getActor(String actorId, String targetUrl) {
		// Create object for user class..it include all user details
		User user = new User();
		// Initialize postResp - response from ocean network is given to this
		// variable
		String getResp = null;
		/**
		 * Used for getting the data to ocean network
		 */
		try {
			String targetURL = targetUrl + actorId;
			GetMethod get = new GetMethod(targetURL);
			HttpClient httpclient = new HttpClient();
			httpclient.executeMethod(get);
			// Response from ocean network
			getResp = get.getResponseBodyAsString();
			/**
			 * Used for return a Json Object with failed result and status
			 */
			if (getResp == null) {
				return user;
			}
			/**
			 * Used for getting WalletId and PrivateKey
			 */
			String prepostToJson = getResp.substring(1, getResp.length() - 1);
			// Data coming from ocean network is a json string..This line remove
			// the "\\" from the response
			String postToJson = prepostToJson.replaceAll("\\\\", "");
			JSONParser parser = new JSONParser();// create json parser
			// parse the data to json object
			JSONObject json = (JSONObject) parser.parse(postToJson);
			// set the wallet id to the user object
			user.setWalletId(json.get("defaultWalletAddress").toString());
			// set the actor id to the user object
			user.setActorId(json.get("actorId").toString());
			// set the updateDatetime to the user object
			user.setUpdateDatetime(json.get("updateDatetime").toString());
			// set the state to the user object
			user.setState((String) json.get("state"));
			// set the creationDatetime to the user object
			user.setCreationDatetime(json.get("creationDatetime").toString());
		} catch (Exception e) {
			e.printStackTrace();
		  }
		return user;
	}

	/**
	 * PUT "/api/v1/keeper/actors/actor/<actor_id>" JSON-encoded key-value pairs
	 * from the Actor schema that are allowed to be updated (only 'name' and
	 * 'attributes')
	 * 
	 * @param targetUrl
	 * @param name
	 * @return
	 *
	 */
	@Override
	public ResponseEntity<Object> updateActor(String targetUrl, String name) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			// setting the headers for the url
			HttpHeaders headers = new HttpHeaders();
			// content-type setting
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			// create a json object to accept the user name
			JSONObject userName = new JSONObject();
			// insert user name to the json object
			userName.put("name", name);
			// create and http entity to attach with the rest url
			HttpEntity<JSONObject> entity = new HttpEntity<>(userName, headers);
			// sent data to ocean network for updating the data
			ResponseEntity<Object> response = restTemplate.exchange(targetUrl, HttpMethod.PUT, entity, Object.class);
			return response;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
