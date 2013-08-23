package gov.nasa.arc.mct.satellite.utilities;

import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.UIManager;


@SuppressWarnings("serial")
public class DisabledComboBoxExample extends JFrame {

  public DisabledComboBoxExample() {
    super("Disabled Item ComboBox Example");
/*
    Object[] items = { new ComboItem("A"), new ComboItem("B", false),
        new ComboItem("1", false), new ComboItem("2", false),
        new ComboItem("abc"), new ComboItem("def") };
*/
    Object[] items = { new ComboItem("A"), new ComboItem("B", false)};
    
    JComboBox combo = new JComboBox(items);
    combo.setRenderer(new ComboRenderer());
    combo.addActionListener(new ComboListener(combo));

    getContentPane().setLayout(new FlowLayout());
    getContentPane().add(combo);
    setSize(300, 100);
    setVisible(true);
  }

  public static void main(String args[]) {
    try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception evt) {}
  
    DisabledComboBoxExample frame = new DisabledComboBoxExample();
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
  }
}
