package UI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

public class DateTimePicker extends JPanel {
    private final JComboBox<Integer> yearBox = new JComboBox<>();
    private final JComboBox<Integer> monthBox = new JComboBox<>();
    private final JComboBox<Integer> dayBox = new JComboBox<>();

    private final JComboBox<Integer> hourBox = new JComboBox<>();
    private final JComboBox<Integer> minuteBox = new JComboBox<>();

    private Runnable onChange;

    public DateTimePicker() {
        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.now();

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.WHITE);

        int currentYear = LocalDate.now().getYear();

        for (int y = currentYear; y <= currentYear + 5; y++) yearBox.addItem(y);
        for (int m = 1; m <= 12; m++) monthBox.addItem(m);
        for (int d = 1; d <= 31; d++) dayBox.addItem(d);

        for (int h = 0; h < 24; h++) hourBox.addItem(h);
        for (int m = 0; m < 60; m++) minuteBox.addItem(m);

        yearBox.setPreferredSize(new Dimension(80, 28));
        monthBox.setPreferredSize(new Dimension(60, 28));
        dayBox.setPreferredSize(new Dimension(60, 28));

        hourBox.setPreferredSize(new Dimension(60, 28));
        minuteBox.setPreferredSize(new Dimension(60, 28));

        setDate(now);
        setTime(time);

        add(yearBox);
        add(new JLabel("-"));
        add(monthBox);
        add(new JLabel("-"));
        add(dayBox);

        add(Box.createHorizontalStrut(10));

        add(hourBox);
        add(new JLabel(":"));
        add(minuteBox);

        yearBox.addActionListener(_ -> {
            updateDays();
            triggerChange();
        });
        monthBox.addActionListener(_ -> {
            updateDays();
            triggerChange();
        });
        dayBox.addActionListener(_ -> triggerChange());

        hourBox.addActionListener(_ -> triggerChange());
        minuteBox.addActionListener(_ -> triggerChange());
    }

    private void updateDays() {
        int y = (int) yearBox.getSelectedItem();
        int m = (int) monthBox.getSelectedItem();

        int max = YearMonth.of(y, m).lengthOfMonth();

        int selected = dayBox.getSelectedItem() != null
                ? (int) dayBox.getSelectedItem()
                : 1;

        dayBox.removeAllItems();

        for (int i = 1; i <= max; i++) {
            dayBox.addItem(i);
        }

        dayBox.setSelectedItem(Math.min(selected, max));
    }


    public LocalDate getDate() {
        if (yearBox.getSelectedItem() == null || monthBox.getSelectedItem() == null || dayBox.getSelectedItem() == null) return null;
        return LocalDate.of(
                (int) yearBox.getSelectedItem(),
                (int) monthBox.getSelectedItem(),
                (int) dayBox.getSelectedItem()
        );
    }

    public LocalTime getTime() {
        if (hourBox.getSelectedItem() == null || minuteBox.getSelectedItem() == null) return null;
        return LocalTime.of(
                (int) hourBox.getSelectedItem(),
                (int) minuteBox.getSelectedItem()
        );
    }

    public void setDate(LocalDate date) {
        yearBox.setSelectedItem(date.getYear());
        monthBox.setSelectedItem(date.getMonthValue());
        updateDays();
        dayBox.setSelectedItem(date.getDayOfMonth());
    }

    public void setTime(LocalTime time) {
        hourBox.setSelectedItem(time.getHour());
        minuteBox.setSelectedItem(time.getMinute());
    }

    public void setOnChange(Runnable r) {
        this.onChange = r;
    }

    private void triggerChange() {
        if (onChange != null) onChange.run();
    }
}