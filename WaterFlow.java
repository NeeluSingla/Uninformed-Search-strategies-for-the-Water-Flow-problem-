
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class WaterFlow {
	

	public static void main(String[] args) throws IOException {
		BufferedReader br = null;
		BufferedWriter bw = null;
		Date startDate = new Date();
		try {
			File file = new File(args[0]);
			FileReader abc = new FileReader(file);
			br = new BufferedReader(abc);
			String line = br.readLine();
			int num = Integer.parseInt(line);
			File outFile = new File("C:\\Users\\Neelu\\Desktop\\output1.txt");
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			FileWriter write = new FileWriter(outFile);
			bw = new BufferedWriter(write);

			for (int i = 0; i < num; i++) {
				String type = br.readLine();
				if (type.equalsIgnoreCase("BFS")) {
					breadthFirstSearch(br, bw);
				} else if (type.equalsIgnoreCase("DFS")) {
					depthFirstSearch(br, bw);
				} else if (type.equalsIgnoreCase("UCS")) {
					uniformCostSearch(br, bw);
				} else {
					bw.write("None");
					bw.newLine();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			br.close();
			bw.close();
			Date endDate = new Date();
			System.out.println(endDate.getTime()-startDate.getTime());
			
		}
	}
	

	private static void uniformCostSearch(BufferedReader br, BufferedWriter bw) throws IOException {
		try {
			String source = br.readLine().trim();
			String destination = br.readLine().trim();
			String middleNode = br.readLine().trim();
			int numb = Integer.parseInt(br.readLine().trim());
			ArrayList<String> graphList = readingGraphs(br, numb);
			int startTime = Integer.parseInt(br.readLine().trim());
			br.readLine();

			sourceNodeValidator(source);
			ArrayList<String> destinationList = destinationNodeValidator(source, destination);
			ArrayList<String> middleList = middleNodeValidator(source, middleNode, destinationList);
			List<String[]> graphListArray = graphListValidation(source, graphList, destinationList, middleList);

			if (startTime < 0) {
				throw new Exception();
			}

			Queue<Node> p = new PriorityQueue<Node>();
			WaterFlow w1 = new WaterFlow();
			WaterFlow.Node sourceNode = w1.new Node();
			sourceNode.setPath(source);
			sourceNode.setLength(startTime);
			p.add(sourceNode);
			Node result = null;
			List<Node> sortList = new ArrayList<Node>();
			Set<String> exploredSet = new HashSet<String>();
			while (p.size() != 0) {

				Node head = p.poll();
				String header = head.getPath().split(",")[head.getPath().split(",").length - 1];
				exploredSet.add(header);
				

				for (String[] pipe : graphListArray) {
					if (Integer.parseInt(pipe[2]) < 0) {
						throw new Exception();
					}

					if (pipe[0].equalsIgnoreCase(header) && destinationList.contains(pipe[1])) {
						if (!isPipeblocked(pipe, head.getLength())) {
							String currentResult = head.getPath() + "," + pipe[1];
							int length = (head.getLength() + Integer.parseInt(pipe[2]));
							WaterFlow.Node resultNode = w1.new Node();
							resultNode.setLength(length);
							resultNode.setPath(currentResult);
							if (result == null) {
								result = resultNode;
							} else if (result.getLength() > resultNode.getLength()) {
								result = resultNode;
							} else if (result.getLength() == resultNode.getLength()) {
								if (pipe[1].compareTo(result.getPath().split(",")[result.getPath().split(",").length-1]) < 0) {
									result = resultNode;
								}
							}
						}
					}

					else if (pipe[0].equalsIgnoreCase(header) && middleList.contains(pipe[1])) {
						if (!(exploredSet.contains(pipe[1]))) {
							if (!isPipeblocked(pipe, head.getLength())) {
								WaterFlow.Node sortedNode = w1.new Node();
								sortedNode.setPath(head.getPath() + "," + pipe[1]);
								sortedNode.setLength((head.getLength() + Integer.parseInt(pipe[2])));
								if(result==null||result.getLength()>sortedNode.getLength()){								
									sortList.add(sortedNode);
								}
							}
						}
					}
				}

				while (sortList.size() > 0) {
					p.add(sortList.get(0));
					sortList.remove(0);
				}
			}
			if (result != null) {
				bw.write(result.getPath().split(",")[result.getPath().split(",").length - 1] + " "
						+ (result.getLength() % 24));
				bw.newLine();
			} else {
				bw.write("None");
				bw.newLine();
			}

		} catch (Exception e) {
			bw.write("None");
			bw.newLine();
		}
	}


	private static void depthFirstSearch(BufferedReader br, BufferedWriter bw) throws IOException {
		try {
			String source = br.readLine().trim();
			String destination = br.readLine().trim();
			String middleNode = br.readLine().trim();
			int numb = Integer.parseInt(br.readLine().trim());
			ArrayList<String> graphList = readingGraphs(br, numb);
			int startTime = Integer.parseInt(br.readLine().trim());
			br.readLine();
			
			sourceNodeValidator(source);
			ArrayList<String> destinationList = destinationNodeValidator(source, destination);
			ArrayList<String> middleList = middleNodeValidator(source, middleNode, destinationList);
			List<String[]> graphListArray = graphListValidation(source, graphList, destinationList, middleList);

			if (startTime < 0) {
				throw new Exception();
			}

			Stack<String> p = new Stack<String>();
			p.push(source);
			String result = null;
			List<String> sortList = new ArrayList<String>();
			Set<String> exploredSet = new HashSet<String>();
			while (p.size() != 0) {
				
				String head = p.pop();
				String header = head.split(",")[head.split(",").length - 1];

				if (!exploredSet.contains(header)) {
					exploredSet.add(header);
				} else {
					continue;
				}
				
				if (destinationList.contains(header)) {
					result = head;
					break;
				} else {
					for (String[] pipe : graphListArray) {
						if (pipe[0].equalsIgnoreCase(header)) {
							if (!exploredSet.contains(pipe[1])) {
								sortList.add(head + "," + pipe[1]);
							}
						}
					}
				}

				while (sortList.size() > 0) {
					String min = sortList.get(0);
					for (String str : sortList) {
						if (str.compareTo(min) > 0) {
							min = str;
						}
					}
					p.push(min);
					sortList.remove(min);
				}
			}

			if (result != null) {
				String[] re = result.split(",");
				bw.write(re[re.length - 1] + " " + ((startTime + re.length - 1) % 24));
				bw.newLine();
			} else {
				bw.write("None");
				bw.newLine();
			}

		} catch (Exception e) {
			bw.write("None");
			bw.newLine();
		}
	}


	private static void breadthFirstSearch(BufferedReader br, BufferedWriter bw) throws IOException {
		try {
			String source = br.readLine().trim();
			String destination = br.readLine().trim();
			String middleNode = br.readLine().trim();
			int numb = Integer.parseInt(br.readLine().trim());
			ArrayList<String> graphList = readingGraphs(br, numb);
			int startTime = Integer.parseInt(br.readLine().trim());
			br.readLine();
			
			sourceNodeValidator(source);
			ArrayList<String> destinationList = destinationNodeValidator(source, destination);
			ArrayList<String> middleList = middleNodeValidator(source, middleNode, destinationList);
			List<String[]> graphListArray = graphListValidation(source, graphList, destinationList, middleList);

			if (startTime < 0) {
				throw new Exception();
			}

			Queue<String> p = new LinkedList<String>();
			p.add(source);
			String result = null;
			List<String> sortList = new ArrayList<String>();
			Set<String> exploredSet = new HashSet<String>();
			while (p.size() != 0) {
				
				String head = p.poll();
				String header = head.split(",")[head.split(",").length - 1];
				exploredSet.add(header);
				

				for (String[] pipe : graphListArray) {
					if (pipe[0].equalsIgnoreCase(header) && destinationList.contains(pipe[1])) {
						String currentResult = head + "," + pipe[1];
						if (result == null) {
							result = currentResult;
						} else if (result.split(",").length > currentResult.split(",").length) {
							result = currentResult;
						} else if (result.split(",").length == currentResult.split(",").length) {
							if (result.compareTo(currentResult) > 0)  {
								result = currentResult;
							}
						}
					}
					else if (pipe[0].equalsIgnoreCase(header) && middleList.contains(pipe[1])) {
						if (!(exploredSet.contains(pipe[1]))) {
							sortList.add(head + "," + pipe[1]);
						}
					}
				}

				if (result != null) {
					sortList.clear();
				} else {
					while (sortList.size() > 0) {
						String min = sortList.get(0);
						for (String str : sortList) {
							if (str.compareTo(min) < 0) {
								min = str;
							}
						}
						p.add(min);
						sortList.remove(min);
					}
				}
			}
			if (result != null) {
				String[] re = result.split(",");
				bw.write(re[re.length - 1] + " " + ((startTime + re.length - 1) % 24));
				bw.newLine();
			} else {
				bw.write("None");
				bw.newLine();
			}

		} catch (Exception e) {
			bw.write("None");
			bw.newLine();
		}
	}

	private static ArrayList<String> destinationNodeValidator(String source, String destination) throws Exception {
		String[] allDestinations = destination.split(" ");
		ArrayList<String> destinationList = new ArrayList<String>();
		if (destination != null && allDestinations.length >= 1 && !destination.isEmpty()) {
			for (String ds : allDestinations) {
				if (ds.equalsIgnoreCase(source)) {
					throw new Exception();
				} else if( ds.equals(ds.toUpperCase())){
					destinationList.add(ds);
				}
			}
		} else {
			throw new Exception();
		}
		return destinationList;
	}

	private static ArrayList<String> middleNodeValidator(String source, String middleNode,
			ArrayList<String> destinationList) throws Exception {
		String[] allMiddleNodes = middleNode.split(" ");
		ArrayList<String> middleList = new ArrayList<String>();
		if (middleNode != null && allMiddleNodes.length >= 1) {
			for (String md : allMiddleNodes) {
				if (destinationList.contains(md) || (md.equalsIgnoreCase(source) )) {
					throw new Exception();
				} else if(md.equals(md.toUpperCase())){
					middleList.add(md);
				}
			}
		} else {
			throw new Exception();
		}
		return middleList;
	}

	private static ArrayList<String> readingGraphs(BufferedReader br, int numb) throws IOException {
		ArrayList<String> graphList = new ArrayList<String>();
		for (int j = 0; j < numb; j++) {
			String graphs = br.readLine();
			graphList.add(graphs);
		}
		return graphList;
	}

	private static List<String[]> graphListValidation(String source, ArrayList<String> graphList,
			ArrayList<String> destinationList, ArrayList<String> middleList) throws Exception {
		List<String[]> graphListArray = new ArrayList<String[]>();
		for (String graph : graphList) {
			String[] gp = graph.split(" ");
			if (gp.length > 3) {
				if (!(gp[0].equalsIgnoreCase(source) || middleList.contains(gp[0])
						|| gp[0].equals(gp[0].toUpperCase()))) {
				} else if (!(destinationList.contains(gp[1]) || middleList.contains(gp[1])
						|| gp[1].equals(gp[1].toUpperCase()))) {
				} else {
					graphListArray.add(gp);
				}
			} else {
				throw new Exception();
			}
		}
		return graphListArray;
	}

	private static void sourceNodeValidator(String source) throws Exception {
		if (source == null || source.split(" ").length != 1 || source.isEmpty()
				|| !source.equals(source.toUpperCase())) {
			throw new Exception();
		}
	}

	private static boolean isPipeblocked(String[] pipe, int size) {
		Set<Integer> s = new HashSet<Integer>();
		for (int i = 4; i < pipe.length; i++) {
			String[] a = pipe[i].split("-");
			int start = Integer.parseInt(a[0]) % 24;
			int end = Integer.parseInt(a[1]) % 24;
			for (int j = start; j <= end; j++) {
				s.add(j);
			}
		}

		if (s.contains(size % 24)) {
			return true;
		}
		return false;
	}

	public class Node implements Comparable<Node> {
		String path;
		int length;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		@Override
		public int compareTo(Node o1) {
			if (this.getLength() > o1.getLength()) {
				return 1;
			} else if (this.getLength() < o1.getLength()) {
				return -1;
			} else {
				if (this.getPath().compareTo(o1.getPath()) < 0) {
					return -1;
				} else if (this.getPath().compareTo(o1.getPath()) > 0) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
	
}

