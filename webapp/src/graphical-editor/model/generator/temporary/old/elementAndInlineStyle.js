//OLD
import {getStyle} from "./style";

export function getShapeStyle(elementName) {
    var style = {};
    switch(elementName) {

        case "klasse":

            style['rect.0b800d24-7d92-4fad-89df-3203d277fe4f'] = getStyle('(child_of -> XX)');
            style['text.e477df6c-e8da-462e-9dc3-2f88d830547f'] = getStyle('(child_of -> XX)').text;
            style['.e477df6c-e8da-462e-9dc3-2f88d830547f'] = getStyle('(child_of -> XX)').text;

            style['rect.d62b0c84-6348-4cd3-b308-508d25012db9'] = getStyle('(child_of -> XX)');
            style['text.ade81ec0-d7d0-44e0-ab85-0e6253c45bc1'] = getStyle('(child_of -> XX)').text;
            style['.ade81ec0-d7d0-44e0-ab85-0e6253c45bc1'] = getStyle('(child_of -> XX)').text;

            style['rect.cc40a695-e82d-4ea7-b5e6-86ac2ae249bd'] = getStyle('(child_of -> XX)');
            style['text.90a7d93a-5efd-4c40-a90f-e74a9f76bfe3'] = getStyle('(child_of -> XX)').text;
            style['.90a7d93a-5efd-4c40-a90f-e74a9f76bfe3'] = getStyle('(child_of -> XX)').text;

            break;

        case "abstractKlasse":

            style['rect.f4a773b8-fa32-4c6e-a5e3-30d742ff5cbb'] = getStyle('(child_of -> XX)');
            style['text.b5762097-dfcf-41a9-8b11-2190c618e6e9'] = getStyle('(child_of -> XX)').text;
            style['.b5762097-dfcf-41a9-8b11-2190c618e6e9'] = getStyle('(child_of -> XX)').text;

            style['rect.bd0fa679-b080-4d84-9eeb-fe7ae99a42cd'] = getStyle('(child_of -> XX)');
            style['text.0685d1f3-9273-42f9-b15f-34ea4a6be378'] = getStyle('(child_of -> XX)').text;
            style['.0685d1f3-9273-42f9-b15f-34ea4a6be378'] = getStyle('(child_of -> XX)').text;

            style['rect.8586b658-768a-4273-b366-d4f1597c561e'] = getStyle('(child_of -> XX)');
            style['text.60cee325-f76b-4d41-b08f-e51427aadf66'] = getStyle('(child_of -> XX)').text;
            style['.60cee325-f76b-4d41-b08f-e51427aadf66'] = getStyle('(child_of -> XX)').text;

            break;

        case "interface":

            style['rect.9461a54c-fbb2-49a2-94ac-77848fbc1f88'] = getStyle('(child_of -> XX)');
            style['text.418aa18b-d386-4d43-b74e-9b0701ef2dee'] = getStyle('(child_of -> XX)').text;
            style['.418aa18b-d386-4d43-b74e-9b0701ef2dee'] = getStyle('(child_of -> XX)').text;

            style['rect.73e17224-4508-463a-a388-c299a5adde76'] = getStyle('(child_of -> XX)');
            style['text.5215ddbc-bcb1-414d-878c-4cea63c06ff5'] = getStyle('(child_of -> XX)').text;
            style['.5215ddbc-bcb1-414d-878c-4cea63c06ff5'] = getStyle('(child_of -> XX)').text;

            style['rect.75f2205e-9a60-45e9-9d33-98e84de80d66'] = getStyle('(child_of -> XX)');
            style['text.2636f960-1374-46ab-b6a0-fc8e2cb3d80d'] = getStyle('(child_of -> XX)').text;
            style['.2636f960-1374-46ab-b6a0-fc8e2cb3d80d'] = getStyle('(child_of -> XX)').text;

            break;

        default:
            style = {};
            break;
    }
    return style;
}

