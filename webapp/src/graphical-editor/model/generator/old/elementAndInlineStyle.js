//OLD
import {getStyle} from "./style";
export function getShapeStyle(elementName) {

    var style = {};
    switch (elementName) {

        case "klasse":

            style['rect.5c487482-2943-4980-8b65-b14498c354ba'] = getStyle('(child_of -> XX)');
            style['text.5c264f7b-4821-4dd5-9798-70c44b8333ab'] = getStyle('(child_of -> XX)').text;
            style['.5c264f7b-4821-4dd5-9798-70c44b8333ab'] = getStyle('(child_of -> XX)').text;

            style['rect.7b5ada00-b651-45cf-a008-915ecd505140'] = getStyle('(child_of -> XX)');
            style['text.5d37c65b-879b-41a9-9afa-391b81d89368'] = getStyle('(child_of -> XX)').text;
            style['.5d37c65b-879b-41a9-9afa-391b81d89368'] = getStyle('(child_of -> XX)').text;

            style['rect.f48cfa94-c651-4ff2-a2bb-cb3eec43d065'] = getStyle('(child_of -> XX)');
            style['text.76794e3f-0e79-4f5f-8bf2-c84ba07327e0'] = getStyle('(child_of -> XX)').text;
            style['.76794e3f-0e79-4f5f-8bf2-c84ba07327e0'] = getStyle('(child_of -> XX)').text;

            break;

        case "abstractKlasse":

            style['rect.b39e7aa2-a142-4dea-9013-52bd0eab784d'] = getStyle('(child_of -> XX)');
            style['text.8ea01922-fbdf-4671-8bce-ce10658c49da'] = getStyle('(child_of -> XX)').text;
            style['.8ea01922-fbdf-4671-8bce-ce10658c49da'] = getStyle('(child_of -> XX)').text;

            style['rect.1780c339-5031-4181-9aa6-53294cfa4310'] = getStyle('(child_of -> XX)');
            style['text.3324620f-e3be-4809-b404-c46be93ece5a'] = getStyle('(child_of -> XX)').text;
            style['.3324620f-e3be-4809-b404-c46be93ece5a'] = getStyle('(child_of -> XX)').text;

            style['rect.163911d7-57f3-4f45-a434-e6d431025885'] = getStyle('(child_of -> XX)');
            style['text.7f1a0b19-b973-494a-9f99-cf52011eab4d'] = getStyle('(child_of -> XX)').text;
            style['.7f1a0b19-b973-494a-9f99-cf52011eab4d'] = getStyle('(child_of -> XX)').text;

            break;

        case "interface":

            style['rect.9f393e03-e371-41a9-8065-a62b13991229'] = getStyle('(child_of -> XX)');
            style['text.f01bed45-534c-40c2-a17e-094afb22fc9d'] = getStyle('(child_of -> XX)').text;
            style['.f01bed45-534c-40c2-a17e-094afb22fc9d'] = getStyle('(child_of -> XX)').text;

            style['rect.1d879b3a-cfa3-4955-99ff-6af1f5bf2ced'] = getStyle('(child_of -> XX)');
            style['text.94cca154-a761-4824-9cc2-bb20a53e6cf9'] = getStyle('(child_of -> XX)').text;
            style['.94cca154-a761-4824-9cc2-bb20a53e6cf9'] = getStyle('(child_of -> XX)').text;

            style['rect.d89d5ff7-f9f1-4816-aa59-8d5df9c27424'] = getStyle('(child_of -> XX)');
            style['text.436e2eb9-27e7-493d-a527-fc1bf54616a6'] = getStyle('(child_of -> XX)').text;
            style['.436e2eb9-27e7-493d-a527-fc1bf54616a6'] = getStyle('(child_of -> XX)').text;

            break;

        default:
            style = {};
            break;
    }
    return style;
}
    