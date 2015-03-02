package com.davisosa.structura;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.davisosa.structura.R;
import com.melnykov.fab.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.os.Handler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LinkedListPlayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LinkedListPlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkedListPlayFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    FloatingActionButton addNodeBtn;
    FloatingActionButton delNodeBtn;
    FloatingActionButton searchNodeBtn;

    NodeView nv;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LinkedListPlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LinkedListPlayFragment newInstance() {
        LinkedListPlayFragment fragment = new LinkedListPlayFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public LinkedListPlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.fragment_linked_list_play, container, false);
        ViewGroup.LayoutParams lp = fl.getLayoutParams();

        nv = new NodeView(getActivity());
        fl.addView(nv, lp.width, lp.height);

        addNodeBtn = (FloatingActionButton) fl.findViewById(R.id.add);
        addNodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nv.addNewNodeToCanvas();
            }
        });

        delNodeBtn = (FloatingActionButton) fl.findViewById(R.id.sub);
        delNodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nv.deleteNode();
            }
        });

        searchNodeBtn = (FloatingActionButton) fl.findViewById(R.id.search);
        searchNodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });

        return fl;
    }

    private void showSearchDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Find a Node");
        alert.setMessage("Enter the value of the node you'd like to find");

        final EditText input = new EditText(getActivity());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                try {
                    nv.findNode(Integer.parseInt(value));
                } catch (Exception e) {
                    // show toast
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }


    public class NodeView extends View {
        int count = 0;
        int defaulttNodeTop = 50;
        int defaultNodeLeft = 120;
        List<LLNode> nodeList = new ArrayList<LLNode>();
        LLNode currentSearchNode = null;

        public NodeView(Context context) {
            super(context);
        }

        public void addNewNodeToCanvas() {
            count++;
            LLNode node = createNewNode();
            nodeList.add(node);
            invalidate();
        }

        public LLNode createNewNode() {
            int topOffset = (int) (defaulttNodeTop + (250 * ( Math.floor((double)nodeList.size()/3) )));
            int leftOffset;
            if (nodeList.size() < 3) {
                leftOffset = defaultNodeLeft + (300 * (nodeList.size()));
            } else {
                if ( Math.floor(nodeList.size()/3) % 2 == 1 ) {
                    leftOffset = (int) (defaultNodeLeft + (300 * Math.abs(2 - (nodeList.size() - (3 * (Math.floor(nodeList.size() / 3)))))));
                } else {
                    leftOffset = (int) (defaultNodeLeft + (300 * (nodeList.size() - (3 * (Math.floor(nodeList.size() / 3))))));
                }
            }

            Random rand = new Random();
            int rand_num = rand.nextInt((100 - 1) + 1) + 1;
            int value = rand_num;
            // TODO: convert to enum
            int arrowDirection = 0; // -1 -> none, 0 -> horizontal, 1 -> vertical, 3 -> both
            if ( Math.floor(nodeList.size()/3) % 2 == 0 ) {
                if ( (nodeList.size()+1 - 3) % 3 == 0 ) {
                    arrowDirection = 1;
                }
            } else {
                if ( (nodeList.size() - (3*(Math.floor(nodeList.size() / 3)))) == 2 ) {
                    arrowDirection = 3;
                } else if ( ( nodeList.size() - (3*(Math.floor(nodeList.size() / 3)))) == 0 ) {
                    arrowDirection = -1;
                }
            }
            Bitmap bitmap = getNodeBitmap(arrowDirection, value);

            return new LLNode(bitmap, value, leftOffset, topOffset);
        }

        public void deleteNode() {
            if (nodeList.size() == 0) {
                return;
            }
            nodeList.remove(nodeList.size() - 1);
            invalidate();
        }

        public void findNode(int value) {
            Boolean found = false;
            int index = 0;
            Handler handler = new Handler();

            while (!found && index < nodeList.size()) {
                final int finalIndex = index;
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {
                        highlightNode(finalIndex);
                    };
                };

                handler.postDelayed(runnable, 2000*index);
                if (nodeList.get(index).value == value) {
                    found = true;
                    System.out.println("FOUND!!!");
                    handler.removeCallbacks(runnable);
                    highlightNode(index);

                }
                index++;
            }
        }

        public void highlightNode(int index) {
            LLNode currentNode = nodeList.get(index);
            Bitmap bitmap = currentNode.bitmap;

            Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth()-100, bitmap.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawColor(Color.argb(100, 57, 202, 116));

            currentSearchNode = new LLNode(newBitmap, 0, currentNode.leftOffset, currentNode.topOffset);
            invalidate();
        }

        public Bitmap getNodeBitmap(int arrowDirection, int nodeValue) {
            View node = getActivity().getLayoutInflater().inflate(R.layout.ll_node, null);

            TextView tvNodeValueDigit = (TextView) node.findViewById(R.id.tvNodeValueDigit);
            tvNodeValueDigit.setText(String.format("%02d", nodeValue));

            node.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            node.layout(0, 0, node.getMeasuredWidth(), node.getMeasuredHeight());

            int width = node.getMeasuredWidth() + 100;
            int height = (arrowDirection == 1 || arrowDirection == 3) ? node.getMeasuredHeight() + 50 : node.getMeasuredHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            node.draw(canvas);

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(6);
            if (arrowDirection == 0) {
                canvas.drawLine(node.getMeasuredWidth() - 15, 120, node.getMeasuredWidth() + 70, 120, paint);
            } else if (arrowDirection == 1) {
                canvas.drawLine(node.getMeasuredWidth() - 30, 120, node.getMeasuredWidth() - 30, 300, paint);
            } else if (arrowDirection == 3) {
                canvas.drawLine(node.getMeasuredWidth() - 15, 120, node.getMeasuredWidth() + 70, 120, paint);
                canvas.drawLine(node.getMeasuredWidth() - 30, 120, node.getMeasuredWidth() - 30, 300, paint);
            }


            return bitmap;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.save();
            for (int i = 0; i < nodeList.size(); i++) {
                canvas.drawBitmap(nodeList.get(i).bitmap, nodeList.get(i).leftOffset, nodeList.get(i).topOffset, null);
            }
            if (currentSearchNode != null) {
                canvas.drawBitmap(currentSearchNode.bitmap, currentSearchNode.leftOffset, currentSearchNode.topOffset, null);
            }
            canvas.restore();
        }
    }

}
