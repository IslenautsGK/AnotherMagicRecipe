package com.anotherera.magicrecipe.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;

public class GuiSelectResearch extends GuiScreen {

	private GuiArcaneWorkbench gaw;
	private List<ResearchItem> researchs;
	private int page;
	private int pageSize;

	public GuiSelectResearch(GuiArcaneWorkbench gaw) {
		this.gaw = gaw;
		researchs = new ArrayList<>();
		for (ResearchCategoryList categoryList : ResearchCategories.researchCategories.values()) {
			for (ResearchItem researchItem : categoryList.research.values()) {
				researchs.add(researchItem);
			}
		}
		page = 0;
	}

	@Override
	public void initGui() {
		super.initGui();
		pageSize = (this.height - 30) / 20;
		updateButton();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		switch (button.id) {
		case -1:
			page--;
			if (page < 0) {
				page = 0;
			}
			updateButton();
			break;
		case -2:
			page++;
			if (page * pageSize >= researchs.size()) {
				page--;
			}
			updateButton();
			break;
		case -3:
			this.mc.displayGuiScreen(gaw);
			break;
		default:
			gaw.setSelectResearch(researchs.get(button.id).key);
			this.mc.displayGuiScreen(gaw);
		}
	}

	private void updateButton() {
		this.buttonList.clear();
		for (int i = 0; i < pageSize && page * pageSize + i < researchs.size(); i++) {
			this.buttonList.add(new GuiButton(page * pageSize + i, this.width / 2 - 100, i * 20, 200, 20,
					researchs.get(page * pageSize + i).getName()));
		}
		this.buttonList.add(new GuiButton(-1, this.width / 2 - 100,
				pageSize * 20 + (this.height - pageSize * 20) / 2 - 10, 50, 20, "上一页"));
		this.buttonList.add(new GuiButton(-2, this.width / 2 + 50,
				pageSize * 20 + (this.height - pageSize * 20) / 2 - 10, 50, 20, "下一页"));
		this.buttonList.add(new GuiButton(-3, this.width / 2 - 40,
				pageSize * 20 + (this.height - pageSize * 20) / 2 - 10, 80, 20, "返回"));
	}

}
