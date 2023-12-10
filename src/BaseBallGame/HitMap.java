package BaseBallGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class HitMap extends JFrame {

	boolean swing = false; //베트를 휘둘렀는지 논리변수.
	int inningCount = 0; //이닝 카운트 
	int strikeCount = 0; //스트라이크 카운트
	int outCount = 0; //아웃 카운트
	ImageIcon normalBatIcon = new ImageIcon("images/bat.png"); // 기본 베트 이미지
	ImageIcon swingBatIcon = new ImageIcon("images/swingBat.gif");
	Field field_Frame;
	JLabel b_ball = new JLabel(new ImageIcon("images/b_ball.png"));
	JLabel[] countLight = new JLabel[4]; // 볼 스트라이크 아웃 카운트 레이블 배열
	JLabel hitZone = new JLabel(normalBatIcon);
	ballThread ballthread; // 공 날라오는 스레드
	CountDownThread countDownThread; // 공 던지기 제어 스레드. 3초간의 카운트다운 이후 투구.
	JLabel lbl; //  볼 -스트- 아웃 알려주는 레이블
	private JLabel countdownLabel=new JLabel(" "); // 투수가 공 던지기까지 대기하는 와인드업 타임. 즉 공이 투구되기까지의 카운트 다운 출력 레이블.
	private JLabel noticeLabel=new JLabel("투수가 준비중입니다..."); // 투수 와인드업 알림 레이블
	String[] hitTypeArray = { "single",  "single", "single", "single", "single",
			"double","double","double",
			"homerun",
			"flyout","flyout","flyout","flyout",
			"groundout","groundout","groundout","groundout" }; // 타격 성공시, 타격의 종류를 나타내는 문자열 배열.
	
	
	
	//상대팀 공격 이닝 자동화 알고리즘 메서드.
	//
	public void opponentScore_Algorithm() {
		
		
	}
	
	//이닝 처리 알고리즘
	public void inningAlgorithm() {
		inningCount++;
		countDownThread.pauseThread();													  // 이닝 종료 후, 해당 이닝 점수판 점수를 0으로 초기화 시키기위해 추가 구현.
		Timer timer = new Timer(2000, new ActionListener() {  //타이머 스레드간의 동기를 맞추기위해 2초 간의 타이머 스레드 시작.
			@Override
			public void actionPerformed(ActionEvent e) {
				field_Frame.scoreLabel[inningCount].setText("0");
			}
		});

		timer.setRepeats(false);
		timer.start();
		//상대 팀 공격 자동화. showmessageDialog() 로 상대팀 득점 결과 나오도록
		
		if(inningCount==8) {
			System.exit(0);
		}
	}
	
	//스트라이크 처리 알고리즘
	public void strikeAlgorithm() {
	    strikeCount++; // 스트라이크 카운트 추가
	    ballthread.pauseThread();
	    b_ball.setLocation(500, 0);
	    
	    if (strikeCount <= 2) {
	        countLight[strikeCount - 1].setVisible(true); // 스트라이크 카운트 라이트 켜기
	        lbl.setText("STRIKE "+strikeCount);
	    } 
	    
	    else if (strikeCount == 3) { // 3 스트라이크 => 아웃 카운트 ++
	        strikeCount = 0; // 스트라이크 카운트 초기화
	        countLight[0].setVisible(false);
	        countLight[1].setVisible(false);
	        outCount++;
	        
	        if (outCount <= 2) {
	            countLight[1 + outCount].setVisible(true); // 아웃 카운트 라이트 켜기
	            lbl.setText("OUT "+outCount);
	        } 
	        else if (outCount == 3) {
	            lbl.setText("inning end!!!");
	            outCount = 0; // 아웃 카운트 초기화
	            countLight[2].setVisible(false);
	            countLight[3].setVisible(false);
	            field_Frame.reset_Base(); // 이닝 종료. 베이스 비어있도록 구현
	            inningAlgorithm();
	        }

	    }
	    hitZone.setIcon(normalBatIcon);
	}
	//아웃 처리 알고리즘
	public void outAlgorithm() {
	   
	        outCount++; // 아웃 카운트 증가
	        strikeCount = 0; // 스트라이크 카운트 초기화
	        ballthread.pauseThread();
	        b_ball.setLocation(500, 0);
	        
	        if (outCount <= 2) {
	            countLight[1 + outCount].setVisible(true); // 아웃 카운트 라이트 켜기
	            lbl.setText("OUT "+outCount);
	        } 
	        else if (outCount == 3) {
	            lbl.setText("inning end!!!");
	            outCount = 0; // 아웃 카운트 초기화
	            countLight[2].setVisible(false);
	            countLight[3].setVisible(false);
	            field_Frame.reset_Base();
	            inningAlgorithm();
	        }
	        
	        for(int i = 0; i < 2; i++) {
	            countLight[i].setVisible(false); // 스트라이크 카운트 라이트 끄기
	        }
	        hitZone.setIcon(normalBatIcon);
	    }
	
	
	//베트를 휘두르지 않았을 경우, 즉 루킹 스트라이크 당했을경우 스트라이크 처리를 위한 메서드
	public void strikeTimestop_swingN(int second) {
		Timer timer = new Timer(second, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				strikeAlgorithm();
				countDownThread.resumeThread();
			}
		});

		timer.setRepeats(false);
		timer.start();

	}
	//베트를 휘둘렀을 경우, 즉 헛스윙한 경우 스트라이크 처리를 위한 메서드.
	//h 키를 눌러서 swing 값이 true 가 된다음, 다시 false 로 값을 바꿔줘야하기에.
	//h 키를 눌러 swing 값이 true가 된 후에 false 로 다시 안바꿔주면 swing 변수는 계속 true 값이되어
	//헛스윙 + 루킹 스트라이크 중복되어 strikeAlgorithm()가 동시에 두번 호출되기에 오류가 난다. 
	//so, 별도의 strikeTimestop_sw() 라는 헛스윙시의 별도의 메서드를 만들어준 것이다.
	public void strikeTimestop_swingY(int second) {
		Timer timer = new Timer(second, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				strikeAlgorithm();
				swing=false;
				countDownThread.resumeThread();
			}
		});
		
		timer.setRepeats(false);
		timer.start();
		
	}

	public String random_HitType() { // 랜덤한 타격 종류 선택하여 타격 종류의 문자열을 리턴해주는 메서드
		int randomNum = (int) (Math.random() * hitTypeArray.length);
		return hitTypeArray[randomNum];
	}
	

	public void timeStop_hit_changeFrame(int second, String s) {
		Timer timer = new Timer(second, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String HitType = s;
				swing=false;
				hit_changeFrame();

				if (HitType.equals("single")) {
					field_Frame.single_hit();
					strikeCount=0;
				}
				else if (HitType.equals("double")) {
					field_Frame.double_hit();
					strikeCount=0;
				}
				else if (HitType.equals("homerun")) {
					field_Frame.homerun();
					strikeCount=0;
				}
				else if (HitType.equals("flyout")) {
					field_Frame.flyout();
					outAlgorithm();
				}
				else if (HitType.equals("groundout")) {
					field_Frame.groundout();
					outAlgorithm();
				}

				/*
				 * else if(random_HitType().equals("fly_out")) field_Frame.fly_out(); else
				 * field_Frame.ground_out();
				 */

				// 이러면 2초 대기후 화면 전환 후 => field 에서 타격으로인한 베이스 진루 적용 후 4.5 대기 후 다시 이곳으로 전환됨.
				// 추후, random 으로 sigle or double or 홈런 or 땅볼아웃 플라이아웃으로.
				lbl.setText("ready for the next ball..");
				hitZone.setBackground(new Color(150, 75, 0));
				b_ball.setLocation(500, 0);
				hitZone.setIcon(normalBatIcon);
				
			}
		});

		timer.setRepeats(false);
		timer.start();

	}

	public void hit_changeFrame() { // 탸격 성공시의 야구장 필드 프레임으로 화면 전환 메서드. 1.2 초 뒤에 전환됨.( 타격 결과 확인을 위한 텀)
		
		field_Frame.setVisible(true);
		dispose();
        countLight[0].setVisible(false);
        countLight[1].setVisible(false);
	}

	public void normal_changeFrame() { // 야구장 필드 프레임으로 화면 전환 메서드.

		field_Frame.setVisible(true);
		dispose();
	}

	class HitPanel extends JPanel {
		
		
		Image countboard = new ImageIcon("images/countboard.png").getImage();
		Image hitter = new ImageIcon("images/hitter.png").getImage();
		
		public HitPanel() {
			// TODO Auto-generated constructor stub

			setLayout(null);

			lbl = new JLabel("Play Ball.. get ready");
			lbl.setLocation(600, 150);
			lbl.setSize(400, 200);
			lbl.setForeground(Color.RED);
			lbl.setFont(new Font("jokerman", Font.BOLD, 30));
			this.add(lbl);
			
			countdownLabel.setLocation(150, 180);
			countdownLabel.setSize(100, 50);
			countdownLabel.setForeground(Color.MAGENTA);
			countdownLabel.setFont(new Font("jokerman", Font.BOLD, 30));
			this.add(countdownLabel); 
			noticeLabel.setLocation(0, 250);
			noticeLabel.setSize(350, 100);
			noticeLabel.setForeground(Color.black);
			noticeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 30));
			this.add(noticeLabel); 

			// 야구공 레이블
			b_ball.setSize(30, 30);
			b_ball.setLocation(500, 0);
			add(b_ball);
			// 히트존 레이블
			hitZone.setSize(300, 300);
			hitZone.setLocation(400, 400);
			// hitZone.setBackground(new Color(150, 75, 0));
			// hitZone.setOpaque(true);
			add(hitZone);

			/// 아웃 스트라이크 볼 카운트 레이블들 구성 및 패널에 부착
			for (int i = 0; i < 4; i++) {
				if (i <= 1) {
					countLight[i] = new JLabel(new ImageIcon("images/STRIKE.png")); // countLight 0,1,2 은 볼 카운트 라이트
					countLight[i].setLocation(49 + i * 47, 68);

				} else {
					countLight[i] = new JLabel(new ImageIcon("images/OUT.png"));// countLight 5,6 은 볼 카운트 라이트
					countLight[i].setLocation(185 + i * 44, 68);
				}
				countLight[i].setSize(30, 30);
				add(countLight[i]);
				countLight[i].setVisible(false); // 초기 카운트보드 라이트의 상태는 비어있음.
			}

			setFocusable(true); // HitPanel 에게 키포커스 주기.
			addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {

					if (!swing) { //h 키 연타를 방지하기위해 swing 변수 이용.
								  // 야구에서도 베트는 한번만 스윙가능하니깐.
								  //즉, 논리변수를 통해 베트는 한번만 스윙할 수 있도록 구현하였다.
					if (e.getKeyChar() == 'h') {
						swing=true; // h 키를 눌러서 베트를 스윙함.
						hitZone.setIcon(swingBatIcon);
						
						/// **************** 이 아래 if 문이 바로 타격 성공시 !!! *****************//
						if (b_ball.getY() >= 550 - 30 && b_ball.getY() <= 550 + 5) { // 히트존
																						// 설정.어느정도 Y
																						// 축까지를
																						// 히트존으로
							
							// 즉, 타격 성공 구역으로 설정할지는 -50 +10 수정하여 조절가능.
							ballthread.pauseThread();// 타격 성공시 공이 멈춤.
							// 위에걸 지우고 공이 멈추는게 아니라 날아가도록 구현해보자
							
							
							
							String hitType = random_HitType();
							lbl.setText(hitType);
							timeStop_hit_changeFrame(2000, hitType); // 그리고 필드 화면으로 전환되어 필드 상태 확인.
							// 이제 추가로, 타격 결과를 설정하고, 그에 따라 카운트 보드 및 베이스 진루 설정해야지
							
						} else// 헛스윙. h 키를 눌러서 스윙했지만 타격은 실패한 경우.
						{
							strikeTimestop_swingY(2000);
						}
						
					}

				}
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			g.drawImage(countboard, 0, 0, 400, 100, this);
			g.drawImage(hitter, 650, 450, 100, 200, this);

			
			
		}
	}

	public HitMap(Field field_Frame) {
		// TODO Auto-generated constructor stub
		this.field_Frame = field_Frame;

		setContentPane(new HitPanel());

		this.setSize(1000, 700);
		this.setTitle("타격시작");
		this.setLocation(500, 200);
	}
	
	
	
	/// 공 투구 자동화 카운트다운 스레드
	public class CountDownThread extends Thread {
		
		private boolean paused = true; // 스레드 일시 중지 여부를 체크하는 변수
	    private int count=5;
	    
	    public void pauseThread() { // 공 멈추기 메서드
			paused = true; // 외부에서 호출하여 스레드를 일시 중지
		}

		public void resumeThread() { // 공 던지기 메서드
			countdownLabel.setVisible(true);
			noticeLabel.setOpaque(false);
			noticeLabel.setText("투수가 준비중입니다...");
			synchronized (this) {
				paused = false;
				notify(); // 스레드 재개를 위해 일시 중지 상태 해제
			}
		}

		@Override
		public void run() {
		    while (true) {
		        synchronized (this) {
		            if(paused) {
		                try {
		                    wait();
		                } catch (InterruptedException e) {
		                    return;
		                }
		            }
		        }
		        if(count==1) {
		        	noticeLabel.setOpaque(true);
		        	noticeLabel.setBackground(Color.red);
		        	noticeLabel.setForeground(Color.blue);
		        	noticeLabel.setText("타격을 준비하세요!!!!");
		        }
		        if (count == 0) {
		            ballthread.resumeThread();
		            this.pauseThread();
		            countdownLabel.setVisible(false);
		            count = 5;  // 카운트 다운을 재시작할 수 있도록 count를 초기화
		        }
		        
		        countdownLabel.setText(Integer.toString(count));
		        count--;

		        try {
		            Thread.sleep(1000);
		        } catch (InterruptedException e) {
		            return;
		        }
		    }
		}
	}
	/// 공 스레드
	class ballThread extends Thread {
		Random rand = new Random(); // 랜덤 객체 생성
		int hitZoneCenterX = hitZone.getX() + hitZone.getWidth() / 3; // hitZone의 중점 x 좌표
		// 진폭과 주파수를 랜덤하게 설정

		private boolean paused = true; // 스레드 일시 중지 여부를 체크하는 변수

		public void pauseThread() { // 공 멈추기 메서드
			paused = true; // 외부에서 호출하여 스레드를 일시 중지
		}

		public void resumeThread() { // 공 던지기 메서드
			synchronized (this) {
				paused = false;
				notify(); // 스레드 재개를 위해 일시 중지 상태 해제
			}
		}

		@Override
		public void run() {
			while (true) {
				
				
				synchronized (this) {
					if (paused) { // 일시 중지 플래그가 true이면 실행을 대기
						try {
							wait();
						} catch (InterruptedException e) {
							return;
						}
					}
				}
				
				// 공의 y 좌표를 먼저 증가
				int ballY = b_ball.getY() + 10;
				b_ball.setLocation(b_ball.getX(), ballY);
				 
				if(ballY == 720 && swing==false) { //헛스윙시
					strikeTimestop_swingN(2000);
				}
				
				double amplitude = hitZoneCenterX / 10.0; // 고정된 진폭 값으로 설정
				double frequency = 2 * Math.PI / getHeight() * 0.5; // 고정된 주파수 값으로 설정

				// 공의 x 좌표를 y 좌표의 사인 함수로 설정. hitZone의 중점을 중심으로 휘어져서 이동
				int ballX = hitZoneCenterX - (int) (amplitude * Math.sin(frequency * ballY)); // 사인 함수에 "-1"을 곱해 반전
				b_ball.setLocation(ballX, b_ball.getY());

				try {
					sleep(8); // 한 10~ 1 정도?
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
