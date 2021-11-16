


import javax.swing.JFrame;


public class GameFrame extends JFrame{
    
    GamePanel panel;
   
    
    GameFrame(){
        
         panel = new GamePanel();
        
        this.add(panel);
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null); //centra el JFrame en la pantalla
    }
}

    

    
