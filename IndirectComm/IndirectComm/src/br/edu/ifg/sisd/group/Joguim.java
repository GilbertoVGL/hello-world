package br.edu.ifg.sisd.group;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Joguim extends ReceiverAdapter {
	JChannel channel;
	String user_name = System.getProperty("user.name", "n/a");
	final List<String> state = new LinkedList<String>();
	ArrayList<Integer> lista = new ArrayList<>();
	int numOfMsgs = 0;
	int numOfPlayers;

	private void start() throws Exception {
		System.setProperty("java.net.preferIPv4Stack", "true");
		channel = new JChannel();
		channel.setReceiver(this);
		channel.connect("ChatCluster");
		eventLoop();
		channel.close();
	}

	private void eventLoop() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Quantos jogadores?");
		String line = in.readLine();
		numOfPlayers = Integer.parseInt(line);

		while (numOfPlayers > 2) {
			try {
				System.out.print("> ");
				System.out.flush();
				line = in.readLine();
				int number = Integer.parseInt(line);
				System.out.println(number);				
				addOrRemoveNumber(number);

			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		validaGanhador();
	}
	
	public void receive(Message msg) {
		while(numOfPlayers > numOfMsgs){
			String line = msg.getSrc() + ": " + msg.getObject();
			System.out.println(line);
			numOfMsgs++;
			synchronized (state) {
				state.add(line);
			}
		}
		try {
			validaGanhador();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void viewAccepted(View new_view) {
		System.out.println("** view: " + new_view);
	}
	
	public void getState(OutputStream output) throws Exception {
		synchronized (state) {
			Util.objectToStream(state, new DataOutputStream(output));
		}
	}

	@SuppressWarnings("unchecked")
	public void setState(InputStream input) throws Exception {
		List<String> list = (List<String>) Util.objectFromStream(new DataInputStream(input));
		synchronized (state) {
			state.clear();
			state.addAll(list);
		}
		System.out.println("received state (" + list.size() + " messages in chat history):");
		for (String str : list) {
			System.out.println(str);
		}
	}

	
	public void addOrRemoveNumber(int number) throws Exception{
		if(numOfPlayers > 2){
			synchronized (lista){
				if(!lista.contains(number)){
					lista.add(number);
					Message msg = new Message(null, "lista>" + lista);
					channel.send(msg);
				}else{
					int i = lista.indexOf(number);
					lista.remove(i);
					numOfPlayers = numOfPlayers - 2;
					System.out.println("Numero " + number + " já existente na lista, você perdeu.");
					Message msg = new Message(null, "lista>" + lista);
					channel.send(msg);
				}
			}
		}
	}
	
	public void validaGanhador() throws Exception{
		int maior = Collections.max(lista);
		int menor = Collections.min(lista);
		String line = "Eliminados dessa rodada -> O maior número é: " + maior + ". E o menor é: " + menor + ".";
		Message msg = new Message(null, line);
		if(numOfPlayers > 2){
			synchronized (lista){
				maior = lista.indexOf(maior);
				lista.remove(maior);
				menor = lista.indexOf(menor);
				lista.remove(menor);
				channel.send(msg);
				numOfPlayers = numOfPlayers - 2;
				
			}
		}else{
			System.out.println("Os ganhadores são: " + lista);
		}
		
	}

	public static void main(String[] args) {
		try {
			new Joguim().start();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
