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

	BitmapFont SaucePanDetails;
	Vector3 touch;
	Sprite player,hand;
	Boolean playerOccupied=false;
	String itemName=" ";
	Rectangle playerBounds;
	OrthographicCamera camera;
	ExtendViewport viewport;
	SpriteBatch batch;
	Array<StallObject> stallObjectArray= new Array<StallObject>();
	Array<SaucePan> saucePanArray = new Array<SaucePan>();
	Array<Cup> cupArray=new Array<Cup>();
	Texture recycleBinTexture,milkcupTexture,realHandTexture,backgroundTexture,keepableTexture,stoveTexture,milkSourceTexture,cupSourceTexture,leaveSourceTexture,sugarSourceTexture,gingerSourceTexture,chocolateSourceTexture,saucePanTexture,saucePanFireTexture,handTexture,chocolateTexture,gingerTexture,sugarTexture,leaveTexture;
	int windowWidth=800,windowHeight=420;

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
		private float x,y,cooked,timeElapsed=0f;
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
			if(cooked==100&&!contents.contains("cooked",true)){contents.add("cooked");cooked=0f;}

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



		stallObjectArray.addAll(new StallObject(keepableTexture,"keepable",250,0),new StallObject(stoveTexture,"stove",345,170),new StallObject(milkSourceTexture,"milk",495,100));
		stallObjectArray.addAll(new StallObject(cupSourceTexture,"cup",350,25),new StallObject(gingerSourceTexture,"ginger",260,110),new StallObject(leaveSourceTexture,"leaves",260,140),new StallObject(sugarSourceTexture,"sugar",290,140));
		stallObjectArray.addAll(new StallObject(chocolateSourceTexture,"chocolate",290,110),new StallObject(recycleBinTexture,"recyclebin",70,0));


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

		camera.position.set(windowWidth/2f,windowHeight/2f,0);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		playerBounds=player.getBoundingRectangle();
		batch.begin();
		batch.draw(backgroundTexture, 0, 0);
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
				}
			}
			}
		}
		for(Cup cup : cupArray){
			cup.render(batch);
		}


		for(SaucePan obj : saucePanArray) SaucePanDetails.draw(batch,""+obj.contents + " "+ obj.quantity,obj.saucePanObj.getX()-obj.contents.size*15f,obj.saucePanObj.getY(),150,10,true);
		if(!playerOccupied){ player.setTexture(handTexture);player.setSize(1,1);player.setColor(1,1,1,0);}
		player.draw(batch);
		hand.draw(batch);
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
		for(Cup cup : cupArray) cup.textureDispose();
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
							playerOccupied=false;
							player.setTexture(handTexture);
							player.setSize(1,1);
							player.setColor(1,1,1,0);
							console("inactive");
							break;
						}
						if(!playerOccupied){
							cup.active=true;
							playerOccupied=true;
							console("active");
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
					Gdx.app.log(""," occupied? "+playerOccupied +" name = "+ itemName +" active?" +obj.active);

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
								console(cupArray.size + "");
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
