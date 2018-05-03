package com.patlejch.messageschedule.binding;

import android.databinding.BindingAdapter;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.ArrayList;
import java.util.List;

public class ChipsInputBinding {

    @BindingAdapter("app:chipsList")
    public static void setChipsList(ChipsInput chipsInput, List<ChipInterface> chips) {

        List<? extends ChipInterface> currentList = chipsInput.getSelectedChipList();
        if (!currentList.isEmpty()) {

            for (ChipInterface chip : chips) {

                boolean add = true;
                for (ChipInterface chipInterface : currentList) {
                    if (chipInterface.getId() == chip.getId()) {
                        add = false;
                    }
                }

                if (add)
                    chipsInput.addChip(chip);
            }
        } else {
            for (ChipInterface chip : new ArrayList<>(chips)) {
                chipsInput.addChip(chip);
            }
        }

    }

}
