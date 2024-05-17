package pcd.lab05.mvc.version3_good;

public class TestGUI {
  static public void main(String[] args){
	  
	MyModel model = new MyModel();
	MyController controller = new MyController(model);
    MyView view = new MyView(controller);
    model.addObserver(view);    
    view.setVisible(true);
    
    new MyAgent(model).start();

  }
  
}
