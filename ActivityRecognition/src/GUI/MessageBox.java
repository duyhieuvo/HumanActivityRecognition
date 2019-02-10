package GUI;

public interface MessageBox {
	//show Information message box
	public void showInfoMes(StringBuilder infoMessage);
	
	//show Warning message box
	public void showWarnMes(StringBuilder warnMessage);
	
	//clear Message box
	public void clearMes(StringBuilder message);
}

