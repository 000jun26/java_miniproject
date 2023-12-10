package BaseBallGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Field extends JFrame{
// private Timer timer; // 시간 지연에 필요한 Timer 객체.
private HitMap Hit_Frame; // Hitmap 프레임
HitMap.ballThread ballthread; // HitMap 클래스의 내부 클래스인 ballThread클래스의 객체 ballthread 를 생성.
HitMap.CountDownThread countDownThread; // HitMap 클래스의 내부 클래스인 CountDownThread클래스의 객체 countDownThread 를 생성.
private String team_name; // 팀 이름
private Color[] baseColors = { Color.WHITE, Color.WHITE, Color.WHITE }; // 타격 결과에 따른 베이스 색 변화를 주기위한 베이스 색 배열.
private Boolean[] cur_Base = { false, false, false }; // 현재 1-2-3 루 베이스에 진출한 주자들의 유-무를 판별해주는 배열.
// 주자 진출 알고리즘에 필수적인 배열이다. true-false 로, 해당 베이스에 주자가 있는지. 아니면 비어있는 베이스인지 판별할 수
// 있다.
// 1-2-3 루 베이스로만 구성(3개의 요소). 홈 베이스는 구성x. 3루에서 일반 진출 혹은 2루에서 2루타로 진출시 그냥 득점으로
// 처리하고
// 기존 주자가 있던 해당 베이스는 false 로하여 비어있도록 표시한다. cur_Base 배열의 각 요소 값을 설정 후 각 베이스의 색깔을
// baseColors 배열에 반영하고 repaint 하여 베이스 색을 최종 변경하여 베이스 상태를 field 프레임에서 눈으로 확인이
// 가능해지는
// 주자 진출 알고리즘을 고안해내었다.
// 당연히 초기 cur_Base 요소들의 값은 false 로, 모든 베이스에는 주자가 없는 상태이다.

private int score[][] = new int[2][9]; //점수를 나타내는 배열 score[0]은 플레이어팀 점수 score[1]은 상대팀 점수
JLabel scoreLabel[][] = new JLabel[2][10];  //점수판에 점수를 나타내는 Label
FileWriter fileWriter2 = null;  //출력을 위한 객체

public void timeStop_changeFrame(int second) {
	Timer timer = new Timer(second, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			changeFrame();
			countDownThread.resumeThread();
		}
	});

	timer.setRepeats(false);
	timer.start();

}

public void changeFrame() { // hit 프레임으로 화면 전환 메서드. 1.2 초 뒤에 전환됨.(주루 상황 표시 후 다시 히트프레임으로 전환)


	Hit_Frame.setVisible(true);
	dispose();
}
public void output(String s) {  //문자열 s를 텍스트 파일로 출력하는 함수. ./file/testscore.txt 만들어놔야함.
	try {
		fileWriter2 = new FileWriter("./file/testscore.txt",true);
		fileWriter2.write(" "+s+" ");
		fileWriter2.close();
		}
	catch(IOException e) {
		System.out.println("입출력 오류");
	}
}
public void initScore() {  //점수 초기화
	for(int j=0;j<score.length;j++) {
		for(int i=0;i<score[0].length;i++)
			score[j][i]=0;
	}
}
public void printScore(int inning) {  //점수판에 점수 출력
	int sum=0,sum2=0;
	scoreLabel[0][inning].setText(Integer.toString(score[0][inning]));
	scoreLabel[1][inning].setText(Integer.toString(score[1][inning]));
	for(int i=0;i<score[0].length;i++) {
		sum += score[0][i];
		sum2 += score[1][i];
	}
	scoreLabel[0][9].setText(Integer.toString(sum));
	scoreLabel[1][9].setText(Integer.toString(sum2));
}

// 이런식으로, 타격 결과에 따라서 현재 각 베이스의 상태를 바꿔준다.cur_Base 요소 값 변경.
public void single_hit(){
	output("1루타");
	if (cur_Base[2] == true) { // 3루에 주자가 있다면 홈으로 들어옴
		cur_Base[2] = false;
		// 점수 추가
		score[0][Hit_Frame.inningCount]++;
	}
	if (cur_Base[1] == true) { // 2루에 주자가 있다면 3루로 진루
		cur_Base[1] = false;
		cur_Base[2] = true;
	}
	if (cur_Base[0] == true) { // 1루에 주자가 있다면 2루로 진루
		cur_Base[0] = false;
		cur_Base[1] = true;
	}
	cur_Base[0] = true; // 타자는 1루로 진루
	set_CurBase();
	printScore(Hit_Frame.inningCount);
}

public void double_hit() {
	output("2루타");
	if (cur_Base[1] == true) { // 3루 혹은 2루에 주자가 있다면 홈으로 들어옴
		cur_Base[1] = false;
		// 점수 추가
		score[0][Hit_Frame.inningCount]++;
	}
	if(cur_Base[2]==true) {
		cur_Base[2]=false;
		score[0][Hit_Frame.inningCount]++;
	}
	if (cur_Base[0] == true) { // 1루에 주자가 있다면 3루로 진루
		cur_Base[0] = false;
		cur_Base[2] = true;
	}
	cur_Base[1] = true; // 타자는 2루로 진루
	
	printScore(Hit_Frame.inningCount);
	set_CurBase();
}
public void triple_hit() {
	output("3루타");
	if (cur_Base[0] == true) {
		cur_Base[0] = false;
		// 점수 추가
		score[0][Hit_Frame.inningCount]++;
	}
	if (cur_Base[1] == true) { 
		cur_Base[1] = false;
		// 점수 추가
		score[0][Hit_Frame.inningCount]++;
	}
	if(cur_Base[2]==true) {
		cur_Base[2]=false;
		score[0][Hit_Frame.inningCount]++;
	}
	cur_Base[2]=true;
	printScore(Hit_Frame.inningCount);
	set_CurBase();
}

public void homerun() {
	output("홈런");
	for (int i = 0; i < 3; i++) {
		if (cur_Base[i] == true) { // 모든 베이스에 주자가 있다면 홈으로 들어옴
			cur_Base[i] = false;
			// 점수 추가
			score[0][Hit_Frame.inningCount]++;
		}
	}
	// 타자도 홈으로 들어와 점수 추가
	score[0][Hit_Frame.inningCount]++;
	printScore(Hit_Frame.inningCount);
	
	set_CurBase();
}
public void flyout() {
	output("뜬공");
	set_CurBase();
}
public void groundout() {
	output("땅볼");
	set_CurBase();
}


// 현재 베이스의 주자 진출 유-무에 따라서 베이스 색을 설정해주는 메서드. 즉, 현재 베이스에 주자 진출 유-무를 확인할 수 있도록해주는
// 메서드.
public void set_CurBase() {
	for (int i = 0; i < cur_Base.length; i++) {
		if (cur_Base[i] == false) // 이 베이스에 주자가 없으면 베이스를 흰색으로 설정.
			baseColors[i] = Color.WHITE;
		else // 이 베이스에 주자가 있으면 베이스를 파란색으로 설정.
			baseColors[i] = Color.BLUE;
	}

	repaint(); // 베이스 색 설정 후, repaint 호출하여 색 변경을 적용시킨다.

	timeStop_changeFrame(2000); // 3초간 시간 지연. 베이스 상태 변경 후 (repaint), hitMap 프레임으로 화면 전환.
	// 모르겠다. hitmap 프레임의 timeStop_hit_changeFrame(2000); 시간 값과, 
	//현재 field 프레임의 timeStop_changeFrame(4500); 의 시간값을 조율하니 잘 된다. 
	//아마도 timer 객체간의 충돌? 이거나 별도의 타이머 스레드 개념이다 보니 병렬적으로 실행되기때문에  
	//타이머가 즉 시간 지연이, sequential 하게 흘러가는것이 아닌것 같다.
	
	//즉, HitMap 프레임에서 timeStop_hit_changeFrame(2000); 이 실행되어 2초간 멈추고 현재 Field 프레임으로 화면 전환이 되는동안
	//그 2초 기다리는 동안에 field_Frame.single_hit(); 이 실행되어 

}

public void reset_Base() {
	for (int i = 0; i < cur_Base.length; i++) {
		baseColors[i] = Color.WHITE;
		cur_Base[i] = false;
	}
	repaint();
}
// -------------경기장 패널------------------//

class fieldPanel extends JPanel {

	private Image basefield = new ImageIcon("images/base_field.jpg").getImage();
	private Image scoreboard = new ImageIcon("images/scoreboard.png").getImage();
	private Vector<int[]> baseX; // 123루+홈 베이스를 구성할때 필요한 배열을 요소로하는 벡터 baseX.
	private Vector<int[]> baseY; // 123루+홈 베이스를 구성할때 필요한 배열을 요소로하는 벡터 baseY.

	// 베이스 배열 구성 메서드
	public void baseArrays() {
		baseX = new Vector<int[]>();
		baseY = new Vector<int[]>();
		int[] x1 = { 660, 620, 660, 700 };
		int[] y1 = { 410, 450, 490, 450 };
		int[] x2 = { 490, 450, 490, 530 };
		int[] y2 = { 270, 310, 350, 310 };
		int[] x3 = { 320, 280, 320, 360 };
		int[] y3 = { 410, 450, 490, 450 };
		// 홈 베이스는 없어도될듯? 3루에서 진루해서 홈 밟는거는 3루 흰색으로 변경해서
		// 홈으로 진루해서 득점한 것으로 표현해도될듯.
		baseX.add(x1);
		baseY.add(y1);
		baseX.add(x2);
		baseY.add(y2);
		baseX.add(x3);
		baseY.add(y3);

	}

	public fieldPanel() {
		// TODO Auto-generated constructor stub

		setLayout(null);
		baseArrays(); // 베이스 배열 구성.
		for(int i=0;i<scoreLabel[0].length;i++) {//점수판에 점수를 나타내는 기능
			for(int j=0;j<scoreLabel.length;j++) {
			scoreLabel[j][i] = new JLabel("");
			scoreLabel[j][i].setSize(30,30);
			if(i==scoreLabel[0].length-1)
				scoreLabel[j][i].setLocation(240+37*i,50*(j+1));
			else
				scoreLabel[j][i].setLocation(224+37*i,50*(j+1));
			scoreLabel[j][i].setFont(new Font("gothic",Font.BOLD , 20));
			scoreLabel[j][i].setForeground(Color.WHITE);
			add(scoreLabel[j][i]);
			}
		}
		
		JButton btn = new JButton("Play Ball");
		btn.setLocation(610, 0);
		btn.setSize(100, 50);
		this.add(btn);
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// 고급 스윙기술 => JOptionPane.showInputDialog 사용하여 팀 이름 입력받음
				 team_name = JOptionPane.showInputDialog("팀 이름을 선택해주세요=>");
				 JLabel lb= new JLabel(team_name);
				 lb.setAlignmentX(CENTER_ALIGNMENT);
				 lb.setAlignmentY(CENTER_ALIGNMENT);
				 lb.setSize(100, 30);
				 lb.setLocation(50, 50);
				 lb.setFont(new Font("gothic",Font.BOLD , 20));
				 lb.setForeground(Color.WHITE);
				 add(lb);
				 JLabel lb2= new JLabel("적팀");
				 lb2.setAlignmentX(CENTER_ALIGNMENT);
				 lb2.setAlignmentY(CENTER_ALIGNMENT);
				 lb2.setSize(100, 30);
				 lb2.setLocation(60, 100);
				 lb2.setFont(new Font("gothic",Font.BOLD , 20));
				 lb2.setForeground(Color.WHITE);
				 add(lb2);
				ballthread.start();
				countDownThread.start();
				Hit_Frame.setVisible(true);
				countDownThread.resumeThread();
				dispose(); // dispose메서드로 현재 field 프레임은 해제하고 HitMap 프레임을 visible하도록하여 화면 전환 효과.
				remove(btn); // btn.setvisible(false) 도 가능.
				scoreLabel[0][0].setText("0");
				// revalidate(); 혹시 제거후 반영 안될 시 프레임 재배열 + 다시 그리기
				// repaint();

			}
		});

	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.drawImage(basefield, 0, 0, this.getWidth(), this.getHeight(), this);
		g.drawImage(scoreboard, 0, 0, 600, 150, this);

		g.setColor(Color.WHITE);

		for (int i = 0; i < baseX.size(); i++) {
			g.setColor(baseColors[i]);
			int[] x = baseX.get(i);
			int[] y = baseY.get(i);
			g.fillPolygon(x, y, 4); // 베이스 그리기.
		}
	}

}

public Field() {
	// TODO Auto-generated constructor stub
	initScore();
	output("1회");
	this.setTitle("한성 스타디움");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setSize(1000, 700);
	// setLocation(500, 200);
	setLocation(500, 200);
	Hit_Frame = new HitMap(this); // newFrame 에게 지금이 startFrame 에 대한 정보를 전달.

	ballthread = Hit_Frame.new ballThread();
	Hit_Frame.ballthread = ballthread; // Hit_Frame 객체의 ballthread스레드를 현재 Field 객체의 ballthread 로 설정.
										// 즉, HitMap 과 Field 가 동일한 ballthread스레드를 공유하게 됐다!!
	countDownThread = Hit_Frame.new CountDownThread();
	Hit_Frame.countDownThread = countDownThread; // Hit_Frame 객체의 countDownThread스레드를 현재 Field 객체의 countDownThread 로 설정.
	                                             // HitMap 과 Field 가 동일한 CountDownThread스레드를 공유

	fieldPanel fPanel = new fieldPanel();
	setContentPane(fPanel);
	setVisible(true);
	printScore(Hit_Frame.inningCount); //처음 점수판에 0표기

}

public static void main(String[] args) {
	// TODO Auto-generated method stub
	Field field_Frame = new Field();
}
}
