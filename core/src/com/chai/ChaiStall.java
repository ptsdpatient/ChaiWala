package com.chai;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Font;
import java.util.Objects;

public class ChaiStall extends ApplicationAdapter {
	BitmapFont SaucePanDetails,MoneyFont;
	Vector3 touch;
	Sprite player,hand,customerArea;
	Boolean playerOccupied=false;
	String itemName=" ";
	String[] menu = {"ginger","sugar","chocolate","leaves","cooked"};
	Rectangle playerBounds,customerAreaBounds;
	OrthographicCamera camera;
	ExtendViewport viewport;
	SpriteBatch batch;
	Texture[] customerArray = new Texture[13];
	Array<StallObject> stallObjectArray= new Array<StallObject>();
	Array<SaucePan> saucePanArray = new Array<SaucePan>();
	Array<Cup> cupArray=new Array<Cup>();
	Array<Customer> customers = new Array<Customer>();
	Texture moneyTexture,customerAreaTexture,recycleBinTexture,milkcupTexture,realHandTexture,backgroundTexture,keepableTexture,stoveTexture,milkSourceTexture,cupSourceTexture,leaveSourceTexture,sugarSourceTexture,gingerSourceTexture,chocolateSourceTexture,saucePanTexture,saucePanFireTexture,handTexture,chocolateTexture,gingerTexture,sugarTexture,leaveTexture;
	int windowWidth=800,windowHeight=420;
	int money=0,gameLevel;
	float worldTimeElapsed=0f;

	public void console(String data){
		Gdx.app.log("",data);
	}
	public class StallObject{
		private float x,y;
		private Rectangle objectBounds;
		private Sprite object;
		private String objectName;
		public StallObject(Texture texture,String objectName,float x,float y){
			this.objectName=objectName;
			this.object = new Sprite(texture);
			this.objectBounds=object.getBoundingRectangle();
			object.setOrigin(object.getWidth()/2,object.getHeight()/2);
			object.setPosition(x,y);

		}
		public void render(SpriteBatch batch){
			object.draw(batch);
		}
		public Rectangle getObjectBounds(){
			objectBounds=object.getBoundingRectangle();
			return objectBounds;
		}
	}
	public class SaucePan{
		private float x,y,cooked=0f,timeElapsed=0f;
		private int quantity=0;
		private boolean active=false;
		private boolean onStove=false;
		private Array<String> contents=new Array<String>();
		private Sprite saucePanObj;
		private Rectangle SaucePanBounds;

		public SaucePan(float x,float y){
			this.x=x;
			this.y=y;
			saucePanObj=new Sprite(saucePanTexture);
			saucePanObj.setPosition(x,y);
		}
		public void render(SpriteBatch batch){
			if(cooked==100&&!contents.contains("cooked",true)){
				contents.add("cooked");
				cooked=0f;
			}

			if(active && playerOccupied) {saucePanObj.setPosition(touch.x-50,touch.y-10);}

			saucePanObj.draw(batch);
			if(onStove && contents.contains("milk",true) && cooked<100){
					float delta = Gdx.graphics.getDeltaTime();
					timeElapsed+=delta;
					if(timeElapsed>13f){
						cooked+=20;
					}
			}
		}
		public Rectangle getSaucePanBounds(){
			this.SaucePanBounds=saucePanObj.getBoundingRectangle();
			return SaucePanBounds;
		}
	}
	public class Cup{
		private float x,y;
		private BitmapFont CupDetails;
		private Rectangle cupBounds;
		private Array<String> contents;
		private Sprite cup;
		private Boolean active=true;
		public Cup(float x, float y, Array<String> contents){
			this.contents=contents;
			this.cup=new Sprite(cupSourceTexture);
			this.contents= new Array<String>(contents);
			cup.setPosition(x,y);
			cup.setOrigin(cup.getWidth()/2,cup.getHeight()/2);
			CupDetails=new BitmapFont();
			CupDetails.setColor(Color.CYAN);
		}
		public void render(SpriteBatch batch){
			if(active){
				x=touch.x-6;
				y=touch.y-8;
			}
			cup.setPosition(x,y);
			cup.draw(batch);
			CupDetails.draw(batch,""+this.contents,x,y,150,10,true);

		}
		public Rectangle getCupBounds(){
			cupBounds=cup.getBoundingRectangle();
			return cupBounds;
		}

		public void textureDispose() {
			CupDetails.dispose();
		}
	}
	public class Customer{

		private float x,y,rotation;
		private Rectangle customerBounds;
		private Sprite customerObj;
		private boolean satisfied=false,stop=false;
		private float timeElapsed=0f;
		private int walkIndex=0,rating=0;
		private BitmapFont customerFont;
		private Array<String> demand;
		public Customer(float x, float y, Array<String> demand){
			this.x=x;
			this.y=y;
			this.rotation=rotation;
			this.demand= new Array<String>(demand);
			this.customerObj=new Sprite(customerArray[0]);
			customerFont=new BitmapFont();
			customerFont.setColor(Color.CYAN);
			customerObj.setPosition(x,y);
			customerObj.setRotation(rotation);
			customerObj.setOrigin(customerObj.getWidth()/2f,customerObj.getHeight()/2f);
		}
		public void render(SpriteBatch batch){
			if(!stop) {
				float delta = Gdx.graphics.getDeltaTime();
				timeElapsed += delta;
				if (walkIndex > 12) walkIndex = 0;
				if (timeElapsed > 0.1f) {
					timeElapsed = 0;
					customerObj.setTexture(customerArray[walkIndex]);
					walkIndex++;
				}

				float centerY=420/2f;
				float centerX=800/2f;
				float angleRad = (float) Math.atan2(centerY - customerObj.getY() , centerX-customerObj.getX());
				float angleDeg = (float) Math.toDegrees(angleRad);
				customerObj.setRotation(angleDeg);
				float directionX = MathUtils.cosDeg(angleDeg);
				float directionY = MathUtils.sinDeg(angleDeg);
				//console("x = "+x+"y = "+y+""+" rot="+angleDeg);
				customerObj.setPosition(customerObj.getX() + directionX, customerObj.getY() + directionY);
			}
			if(customerObj.getBoundingRectangle().overlaps(customerAreaBounds) && walkIndex==3){
				stop=true;
				timeElapsed=0;
				customerObj.setTexture(customerArray[3]);

			}

			if(satisfied){
				float delta = Gdx.graphics.getDeltaTime();
				timeElapsed += delta;
				if (walkIndex > 12) walkIndex = 0;
				if (timeElapsed > 0.1f) {
					timeElapsed = 0;
					customerObj.setTexture(customerArray[walkIndex]);
					walkIndex++;
				}

				float directionX = MathUtils.cosDeg(rotation);
				float directionY = MathUtils.sinDeg(rotation);
				customerObj.setPosition(customerObj.getX() - directionX, customerObj.getY() - directionY);

			}
			customerObj.draw(batch);
			if(stop){
				customerFont.draw(batch,""+demand,customerObj.getX()+customerObj.getWidth()/2,customerObj.getY()+customerObj.getHeight()/2,100,30,true);
				float delta = Gdx.graphics.getDeltaTime();
				timeElapsed += delta;
				if(timeElapsed>10f) {
					satisfied=true;
					if(customerObj.getRotation()>180){
						customerObj.setRotation(customerObj.getRotation()-180);
					}else{
						customerObj.setRotation(customerObj.getRotation()+180);
					}
				}
			}

		}
		public Rectangle getCustomerBounds(){
			customerBounds=customerObj.getBoundingRectangle();
			return customerBounds;
		}

	}
	public void spawnCustomer(){
		Array<String> exampleDemand= new Array<>();
		float x=0,y=0;
		int random = MathUtils.random(0,2);
		switch(random){
			case 0:{x=0;y=MathUtils.random(0,420);break;}
			case 1:{x=MathUtils.random(0,800);y=420;break;}
			case 2:{x=800;y=MathUtils.random(0,420);break;}
			default : break;
		}
		random=(int) MathUtils.random(0,menu.length-1);
		for(int i=0;i<random;i++){
			int randomF=MathUtils.random(0,menu.length-1);
			if(!exampleDemand.contains(menu[randomF],true)){
				exampleDemand.add(menu[randomF]);
			}
		}
		exampleDemand.add("milk");

		customers.add(new Customer(x,y,exampleDemand));
	}
	@Override
	public void resize(int width, int height) {
		viewport.update(width,height,true);
	}

	@Override
	public void create () {
		Gdx.input.setInputProcessor(ChaiInput);
		Gdx.graphics.setWindowedMode(windowWidth,windowHeight);

		//hide cursor
		//Gdx.input.setCursorCatched(true);

		//camera and viewport setup, the resize method is important for scaling in android
		camera=new OrthographicCamera();
		viewport= new ExtendViewport(windowWidth,windowHeight,camera);
		camera.setToOrtho(false,windowWidth,windowHeight);
		viewport.apply();
		batch = new SpriteBatch();

		backgroundTexture = new Texture("background.png");
		keepableTexture = new Texture("keepable.png");
		stoveTexture  = new Texture("stove.png");
		milkSourceTexture  = new Texture("milksource.png");
		cupSourceTexture = new Texture("cupsource.png");
		leaveSourceTexture  = new Texture("leavesource.png");
		sugarSourceTexture = new Texture("sugarsource.png");
		gingerSourceTexture  = new Texture("gingersource.png");
		chocolateSourceTexture = new Texture("chocolatesource.png");
		saucePanTexture = new Texture("saucepan.png");
		saucePanFireTexture = new Texture("saucepan_fire.png");
		handTexture= new Texture("point.png");
		chocolateTexture= new Texture("chocolate.png");
		gingerTexture= new Texture("ginger.png");
		sugarTexture= new Texture("sugar.png");
		leaveTexture= new Texture("leaves.png");
		realHandTexture= new Texture("hand.png");
		milkcupTexture = new Texture("milkcup.png");
		recycleBinTexture=new Texture("recyclebin.png");
		customerAreaTexture=new Texture("customerarea.png");
		moneyTexture=new Texture("money.png");
		MoneyFont=new BitmapFont();
		MoneyFont.setColor(Color.GREEN);
		MoneyFont.getData().setScale(2f);
		customerArea=new Sprite(customerAreaTexture);
		customerArea.setScale(1f);
		customerArea.setPosition(145,0);
		customerArea.setOrigin(customerArea.getWidth()/2f,customerArea.getHeight()/2f);
		customerAreaBounds=customerArea.getBoundingRectangle();

		for(int i = 0;i<13;i++){
			customerArray[i]=new Texture("walk"+(i+1)+".png");
		}

		stallObjectArray.addAll(new StallObject(keepableTexture,"keepable",250,0),new StallObject(stoveTexture,"stove",345,170),new StallObject(milkSourceTexture,"milk",495,100));
		stallObjectArray.addAll(new StallObject(cupSourceTexture,"cup",350,25),new StallObject(gingerSourceTexture,"ginger",260,110),new StallObject(leaveSourceTexture,"leaves",260,140),new StallObject(sugarSourceTexture,"sugar",290,140));
		stallObjectArray.addAll(new StallObject(chocolateSourceTexture,"chocolate",290,110),new StallObject(recycleBinTexture,"recyclebin",70,0));

		spawnCustomer();


		SaucePanDetails=new BitmapFont();
		SaucePanDetails.setColor(Color.CYAN);

		player= new Sprite(handTexture);
		hand=new Sprite(realHandTexture);
		playerBounds=player.getBoundingRectangle();
		saucePanArray.addAll(new SaucePan(400,25));
		player.setColor(1,1,1,0);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		float delta = Gdx.graphics.getDeltaTime();
		worldTimeElapsed+=delta;
		if(customers.size<7&&(worldTimeElapsed>MathUtils.random(10,15))){
			spawnCustomer();
			worldTimeElapsed=0;
		}
		camera.position.set(windowWidth/2f,windowHeight/2f,0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		playerBounds=player.getBoundingRectangle();
		batch.begin();
		batch.draw(backgroundTexture, 0, 0);
		customerArea.draw(batch);
		for(StallObject obj : stallObjectArray){
			obj.render(batch);
		}
		for(SaucePan obj : saucePanArray){
			obj.render(batch);

			for(StallObject stallObject : stallObjectArray){
				if(stallObject.getObjectBounds().overlaps(obj.getSaucePanBounds())&& Objects.equals(stallObject.objectName, "stove")&& !obj.active){
					obj.onStove=true;
					obj.saucePanObj.setTexture(saucePanFireTexture);
				}
			if(obj.getSaucePanBounds().overlaps(stallObject.getObjectBounds()) && Objects.equals(stallObject.objectName, "recyclebin")&& !obj.active&&!playerOccupied){
				if(obj.contents.size>0){
					obj.contents.removeRange(0,obj.contents.size-1);
					obj.quantity=0;
					obj.cooked=0f;
					obj.timeElapsed=0f;
				}
			}
			}
		}


		for(Customer cus : customers){
			cus.render(batch);
			if(cus.satisfied&&((cus.customerObj.getY()>420-10)||(cus.customerObj.getX()<5)||(cus.customerObj.getX()>800-5))){

				customers.removeValue(cus,true);

			}
		}

		for(Cup cup : cupArray){
			cup.render(batch);
		}

		for(SaucePan obj : saucePanArray) SaucePanDetails.draw(batch,""+obj.contents + " "+ obj.quantity,obj.saucePanObj.getX()-obj.contents.size*15f,obj.saucePanObj.getY(),150,10,true);
		if(!playerOccupied){ player.setTexture(handTexture);player.setSize(1,1);player.setColor(1,1,1,0);}
		player.draw(batch);
		hand.draw(batch);
		batch.draw(moneyTexture,800-160,420-50,91/1.5f,52/1.5f);
		MoneyFont.draw(batch,money+"",800-95,420-22);
		batch.end();
		//console(cupArray.size+"");
	}

	@Override
	public void dispose () {
		keepableTexture.dispose();
		stoveTexture.dispose();
		milkSourceTexture.dispose();
		cupSourceTexture.dispose();
		leaveSourceTexture.dispose();
		sugarSourceTexture.dispose();
		gingerSourceTexture.dispose();
		chocolateSourceTexture.dispose();
		saucePanTexture.dispose();
		saucePanFireTexture.dispose();
		batch.dispose();
		backgroundTexture.dispose();
		chocolateTexture.dispose();
		gingerTexture.dispose();
		sugarTexture.dispose();
		leaveTexture.dispose();
		realHandTexture.dispose();
		SaucePanDetails.dispose();
		recycleBinTexture.dispose();
		customerAreaTexture.dispose();
		MoneyFont.dispose();
		moneyTexture.dispose();
		for(Cup cup : cupArray) cup.textureDispose();
		for(Texture tex : customerArray) tex.dispose();
		for(Customer cus : customers) cus.customerFont.dispose();
	}

	InputProcessor ChaiInput = new InputProcessor() {
		@Override
		public boolean keyDown(int keycode) {
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {

			if(Gdx.input.isTouched()){
				touch = new Vector3(screenX,screenY,0);
				camera.unproject(touch);
				player.setPosition(touch.x+ ((playerOccupied && (Objects.equals(itemName, "milk") || Objects.equals(itemName, "cup")))?-6:0),touch.y+ ((playerOccupied && (Objects.equals(itemName, "milk") || Objects.equals(itemName, "cup")))?-8:0));
				hand.setPosition(touch.x,touch.y);
			}

			if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isTouched()){

				for(Cup cup : cupArray){

					if(cup.getCupBounds().overlaps(playerBounds)){
						if(cup.active){
							cup.active=false;
							for(StallObject stallObject : stallObjectArray) {
								if(stallObject.getObjectBounds().overlaps(cup.getCupBounds())&& Objects.equals(stallObject.objectName, "recyclebin")){
									cupArray.removeValue(cup,true);
								}
							}
							for(Customer cus : customers){
								if(cus.getCustomerBounds().overlaps(cup.getCupBounds())){
									for(String cupOpjects : cup.contents){
										if(cus.demand.contains(cupOpjects,true)){
											cus.rating++;
										}
									}
									money+=cus.rating*5f;
									//console(""+money);
									cus.satisfied=true;
									if(cus.customerObj.getRotation()>180){
										cus.customerObj.setRotation(cus.customerObj.getRotation()-180);
									}else{
										cus.customerObj.setRotation(cus.customerObj.getRotation()+180);
									}

									cupArray.removeValue(cup,true);
									//customers.removeValue(cus,true);

								}
							}
							playerOccupied=false;
							player.setTexture(handTexture);
							player.setSize(1,1);
							player.setColor(1,1,1,0);
							break;
						}
						if(!playerOccupied){
							cup.active=true;
							playerOccupied=true;
							//console("active");
							break;
						}
					}
				}

				for(StallObject obj : stallObjectArray){
					if(obj.getObjectBounds().overlaps(playerBounds)){
						if(playerOccupied && Objects.equals(obj.objectName,"recyclebin")){
							player.setTexture(handTexture);
							player.setSize(1,1);
							player.setOrigin(0,0);
							playerOccupied=false;
							player.setColor(1,1,1,0);
						}
						if(Objects.equals(obj.objectName, "leaves") && !playerOccupied){
							player.setTexture(leaveTexture);
							player.setSize(7,11);
							itemName=obj.objectName;
							playerOccupied=true;
							player.setColor(1,1,1,1);
						}else if(Objects.equals(obj.objectName, "ginger")&& !playerOccupied){
							player.setTexture(gingerTexture);
							player.setSize(11,8);
							itemName=obj.objectName;
							playerOccupied=true;
							player.setColor(1,1,1,1);
						}else if(Objects.equals(obj.objectName, "chocolate")&& !playerOccupied){
							player.setTexture(chocolateTexture);
							player.setSize(10,7);
							itemName=obj.objectName;
							playerOccupied=true;
							player.setColor(1,1,1,1);
						}else if(Objects.equals(obj.objectName, "sugar")&& !playerOccupied){
							player.setTexture(sugarTexture);
							player.setSize(8,7);
							itemName=obj.objectName;
							playerOccupied=true;
							player.setColor(1,1,1,1);
						}else if(Objects.equals(obj.objectName, "milk")&& !playerOccupied){
							player.setPosition(touch.x-6, touch.y-8);
							player.setTexture(milkcupTexture);
							player.setSize(15,18);
							player.setOrigin(0,0);
							itemName=obj.objectName;
							playerOccupied=true;
							player.setColor(1,1,1,1);
						}else if(Objects.equals(obj.objectName, "cup")){
							player.setTexture(cupSourceTexture);
							itemName=obj.objectName;
							player.setSize(15,18);
							player.setPosition(touch.x-6, touch.y-8);
							playerOccupied=true;
							player.setColor(1,1,1,1);
						}

					}
				}

				for(SaucePan obj : saucePanArray){
					//Gdx.app.log(""," occupied? "+playerOccupied +" name = "+ itemName +" active?" +obj.active);

					playerBounds=player.getBoundingRectangle();
					if(playerBounds.overlaps(obj.getSaucePanBounds())){

						if(!playerOccupied &&Objects.equals(itemName, "milk") && !obj.active){
							obj.quantity=100;
						}
						if(!playerOccupied &&Objects.equals(itemName, "cup")&&!obj.active){
							if(obj.quantity>0) {
								cupArray.add(new Cup(touch.x - 6, touch.y - 8, obj.contents));
								itemName=" ";
								obj.quantity -= 20;
								//console(cupArray.size + "");
								break;
							}
						}
						if(!playerOccupied && !Objects.equals(itemName, "saucepan") && !obj.active) {
							if(!obj.contents.contains(itemName,true) && !Objects.equals(itemName, "cup")){
								if(Objects.equals(itemName, "milk")) obj.quantity=100;
								if(!Objects.equals(itemName, " ")) obj.contents.add(itemName);
							}
						}

						if(Objects.equals(itemName, "saucepan") &&obj.active&&!playerOccupied) {
							obj.active=false;
							playerOccupied=false;
							break;
						}
						if(!playerOccupied ){
							obj.active=true;
							playerOccupied=true;
							itemName="saucepan";
							obj.saucePanObj.setTexture(saucePanTexture);
						}else{

							if(!obj.contents.contains(itemName,true) && !Objects.equals(itemName, "cup") && !Objects.equals(itemName, "saucepan")){
								if(Objects.equals(itemName, "milk")) obj.quantity=100;
								if(playerOccupied)obj.contents.add(itemName);
							}
							if(Objects.equals(itemName, "cup")){
								if(obj.quantity>0){
								cupArray.add(new Cup(touch.x-6,touch.y-8,obj.contents));
								itemName=" ";
								obj.quantity-=20;
							}}
							obj.active=false;
							playerOccupied=false;
						}
				}

				}
			}
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			touch = new Vector3(screenX,screenY,0);
			camera.unproject(touch);
			player.setPosition(touch.x+ ((playerOccupied && (Objects.equals(itemName, "milk") || Objects.equals(itemName, "cup")))?-6:0),touch.y+ ((playerOccupied && (Objects.equals(itemName, "milk") || Objects.equals(itemName, "cup")))?-8:0));
			hand.setPosition(touch.x,touch.y);
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			touch = new Vector3(screenX,screenY,0);
			camera.unproject(touch);
			player.setPosition(touch.x+ ((playerOccupied && (Objects.equals(itemName, "milk") || Objects.equals(itemName, "cup")))?-6:0),touch.y+ ((playerOccupied && (Objects.equals(itemName, "milk") || Objects.equals(itemName, "cup")))?-8:0));
			hand.setPosition(touch.x,touch.y);
			//Gdx.app.log("","x: "+touch.x+", y: "+touch.y);
			return false;
		}

		@Override
		public boolean scrolled(float amountX, float amountY) {
			return false;
		}
	};
}
