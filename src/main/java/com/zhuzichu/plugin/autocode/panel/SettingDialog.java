package com.zhuzichu.plugin.autocode.panel;

import com.zhuzichu.plugin.autocode.config.ConfigManager;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;

public class SettingDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel lablePackageName;
    private JTextField textFieldPackageName;

    public SettingDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        initView();
        initListener();

    }

    private void initView() {
        setSize(400, 200);
        setLocationRelativeTo(null);
        setModal(false);
        setTitle("Auto Code Setting");
        lablePackageName.setText(ConfigManager.autoCodeConfig.getPackageName());
    }

    private void initListener() {
        buttonCancel.addActionListener(e -> dispose());
        buttonOK.addActionListener(e -> {
            String text = textFieldPackageName.getText();
            if (StringUtils.isBlank(text)) {
                return;
            }
            setPackageName(text);
            lablePackageName.setText(getPackageName());
            dispose();
        });
    }

    private String getPackageName() {
        return ConfigManager.autoCodeConfig.getPackageName();
    }

    private void setPackageName(String text) {
        ConfigManager.autoCodeConfig.setPackageName(text);
    }
}
